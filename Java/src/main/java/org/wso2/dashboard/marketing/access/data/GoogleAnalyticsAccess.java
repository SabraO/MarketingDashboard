package org.wso2.dashboard.marketing.access.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.model.querydata.GoogleAnalyticsData;
import org.wso2.dashboard.marketing.util.Constants;
import org.wso2.dashboard.marketing.util.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleAnalyticsAccess {

	private static final String APPLICATION_NAME = "GAAccess/1.0";
	private static final java.io.File DATA_STORE_DIR =
			new java.io.File(System.getProperty("user.home"), ".store/analytics_sample");
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory dataStoreFactory;
	private static HttpTransport httpTransport;

	private static final String CLIENT_SECRETS_JSON_FILE_PATH = "clientsecrets.path";
	private static final String CLIENT_SECRETS_ID_INITIAL = "Enter";
	private static final String CLIENT_SECRETS_SECRET_INITIAL = "Enter ";
	private static final String USER = "user";
	private static final String ACCOUNT_NAME ="googleanalytics.accountname" ;
	private static final String WEB_PROPERTY_NAME = "googleanalytics.webpropertyname";
	private static final String VIEW_NAME = "googleanalytics.viewname";
	private static final String QUERY_PREFIX = "ga:";
	private static final String QUERY_METRIC = "ga:users";

	private static final String QUERY_CONTINENT_DIMENSION = "continent";
	private static final String QUERY_COUNTRY_DIMENSION = "country";
	private static final String EUROPE = "Europe";
	private static final String CANADA = "Canada";
	private static final String US = "United States";

	private static Log log = LogFactory.getLog(GoogleAnalyticsAccess.class);

	private static Credential authorizeUser() throws IOException {

		FileInputStream fileInputStream = new FileInputStream(Util.getProperty(CLIENT_SECRETS_JSON_FILE_PATH));

		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fileInputStream));
		if (clientSecrets.getDetails().getClientId().startsWith(CLIENT_SECRETS_ID_INITIAL) ||
		    clientSecrets.getDetails().getClientSecret().startsWith(CLIENT_SECRETS_SECRET_INITIAL)) {
			log.error("Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics " +
			          "into Wso2MarketingDashboard/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
				                                        Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY))
						.setDataStoreFactory(dataStoreFactory).build();

		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(USER);
	}

	private static Analytics getAnalyticsServiceObject(Credential credential) {

		return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
		                                                                     .build();
	}

	private static String getGAAccountId(Analytics analytics) throws IOException {

		String accountId = null;

		Accounts accounts = analytics.management().accounts().list().execute();
		List<Account> accountsList = accounts.getItems();

		if (accountsList.isEmpty()) {
			log.error("No accounts found");
		} else {

			for (Account account : accountsList) {
				if (Util.getProperty(ACCOUNT_NAME).equals(account.getName())) {
					accountId = account.getId();
				}
			}
		}

		return accountId;
	}

	private static String getGAWebPropertyId(Analytics analytics, String accountId) throws IOException {

		String webPropertyId = null;

		Webproperties webProperties;

		webProperties = analytics.management().webproperties().list(accountId).execute();
		List<Webproperty> webPropertiesList = webProperties.getItems();

		if (webPropertiesList.isEmpty()) {
			log.error("No Web Properties found");
		} else {

			for (Webproperty webProperty : webPropertiesList) {
				if (Util.getProperty(WEB_PROPERTY_NAME).equals(webProperty.getName())) {
					webPropertyId = webProperty.getId();
				}
			}
		}

		return webPropertyId;
	}

	private static String getGAViewId(Analytics analytics, String accountId, String webPropertyId) throws IOException {

		String viewId = null;

		Profiles views = analytics.management().profiles().list(accountId, webPropertyId).execute();
		List<Profile> viewsList = views.getItems();

		if (viewsList.isEmpty()) {
			log.error("No profiles found");
		} else {

			for (Profile profile : viewsList) {
				if (Util.getProperty(VIEW_NAME).equals(profile.getName())) {
					viewId = profile.getId();
				}
			}
		}

		return viewId;

	}

	private static List<GoogleAnalyticsData> executeGAQuery(Analytics analytics, String viewId, String dimension,
	                                                        String startDate, String endDate) throws IOException {

		List<GoogleAnalyticsData> dataList = new ArrayList<GoogleAnalyticsData>();

		if (viewId == null) {
			log.error("No profiles found.");
		} else {
			GaData gaData = analytics.data().ga().get(QUERY_PREFIX + viewId, startDate, endDate, QUERY_METRIC)
			                         .setDimensions(QUERY_PREFIX + dimension).setMaxResults(200).execute();

			if (gaData.getRows() == null || gaData.getRows().isEmpty()) {
				log.error("No results Found.");
			} else {
				GoogleAnalyticsData data;
				for (List<String> row : gaData.getRows()) {
					data = new GoogleAnalyticsData();
					data.setName(row.get(0));
					data.setValue(Integer.parseInt(row.get(1)));

					dataList.add(data);
				}
			}
		}

		return dataList;
	}

	public static List<RegionCount> getNoOfUsersPerRegion(String startDate, String endDate)
			throws GeneralSecurityException, IOException {

		List<List<GoogleAnalyticsData>> gADataList = getGoogleAnalyticsReportData(startDate, endDate);

		List<GoogleAnalyticsData> continentDataList = gADataList.get(0);
		List<GoogleAnalyticsData> countryDataList = gADataList.get(1);

		List<Integer> userCountList = calculateNoOfUsersPerRegion(continentDataList, countryDataList);

		List<RegionCount> regionCountList = new ArrayList<RegionCount>();

		addRegionData(Constants.TOTAL, userCountList.get(0), regionCountList);
		addRegionData(Constants.EU, userCountList.get(1), regionCountList);
		addRegionData(Constants.NA, userCountList.get(2), regionCountList);
		addRegionData(Constants.ROW, userCountList.get(3), regionCountList);
		addRegionData(Constants.UNCLASSIFIED, userCountList.get(4), regionCountList);

		return regionCountList;

	}

	private static List<List<GoogleAnalyticsData>> getGoogleAnalyticsReportData(String startDate, String endDate)
			throws IOException, GeneralSecurityException {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		Credential credential = authorizeUser();
		Analytics analytics = getAnalyticsServiceObject(credential);
		String accountId = getGAAccountId(analytics);
		String webPropertyId = getGAWebPropertyId(analytics, accountId);
		String viewId = getGAViewId(analytics, accountId, webPropertyId);

		List<GoogleAnalyticsData> continentDataList =
				executeGAQuery(analytics, viewId, QUERY_CONTINENT_DIMENSION, startDate, endDate);
		List<GoogleAnalyticsData> countryDataList = executeGAQuery(analytics, viewId, QUERY_COUNTRY_DIMENSION, startDate, endDate);

		List<List<GoogleAnalyticsData>> gaDataList = new ArrayList<List<GoogleAnalyticsData>>();

		gaDataList.add(continentDataList);
		gaDataList.add(countryDataList);

		return gaDataList;

	}

	private static List<Integer> calculateNoOfUsersPerRegion(List<GoogleAnalyticsData> continentDataList,
	                                                         List<GoogleAnalyticsData> countryDataList) {

		int totalNoOfUsers = 0, europeNoOfUsers = 0, usaCanadaNoOfUsers = 0, unclassifiedNoOfUsers = 0;

		for (GoogleAnalyticsData continent : continentDataList) {
			totalNoOfUsers = totalNoOfUsers + continent.getValue();
			if (EUROPE.equals(continent.getName())) {
				europeNoOfUsers = continent.getValue();
			}
		}

		for (GoogleAnalyticsData country : countryDataList) {
			if (CANADA.equals(country.getName())) {
				usaCanadaNoOfUsers = usaCanadaNoOfUsers + country.getValue();
			} else if (US.equals(country.getName())) {
				usaCanadaNoOfUsers = usaCanadaNoOfUsers + country.getValue();
			}
		}

		int rowNoOfUsers = totalNoOfUsers - europeNoOfUsers - usaCanadaNoOfUsers;

		return Arrays.asList(totalNoOfUsers, europeNoOfUsers, usaCanadaNoOfUsers, rowNoOfUsers, unclassifiedNoOfUsers);

	}

	private static void addRegionData(String region, int noOfUsers, List<RegionCount> regionCountList) {
		RegionCount regionCount = new RegionCount();
		regionCount.setRegion(region);
		regionCount.setNoOfUsers(noOfUsers);
		regionCountList.add(regionCount);
	}

}
