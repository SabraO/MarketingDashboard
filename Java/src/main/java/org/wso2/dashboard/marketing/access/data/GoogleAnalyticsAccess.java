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
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.model.querydata.GoogleAnalyticsData;
import org.wso2.dashboard.marketing.util.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GoogleAnalyticsAccess {

	private static final String APPLICATION_NAME = "GAAccess/1.0";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
	                                                                                    ".store/analytics_sample");
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory dataStoreFactory;
	private static HttpTransport httpTransport;

	private static Log log = LogFactory.getLog(GoogleAnalyticsAccess.class);

	private static Credential authorizeUser() throws IOException {

		String path ="./config/client_secrets.json";
		FileInputStream fileInputStream = new FileInputStream(path);

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
																JSON_FACTORY,new InputStreamReader(fileInputStream));
		if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
			clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
				log.error("Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics " +
				          "into Wso2MarketingDashboard/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
		                                      clientSecrets, Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY))
				                           .setDataStoreFactory(dataStoreFactory).build();

		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	private static Analytics getAnalyticsServiceObject(Credential credential){

		return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
		                                                                                    .build();
	}

	private static String getGAAccountId(Analytics analytics) throws IOException {

		String accountId =null;

		Accounts accounts = analytics.management().accounts().list().execute();
		List<Account> accountsList = accounts.getItems();

		if(accountsList.isEmpty()){
			log.error("No accounts found");
		}
		else{

			for(Account account : accountsList){
				if(account.getName().equals(Util.getGoogleAnalyticsAccountName())){
					accountId = account.getId();
				}
			}
		}

		return accountId;
	}

	private static String getGAWebPropertyId(Analytics analytics,String accountId) throws IOException {

		String webPropertyId = null;

		Webproperties webProperties;

		webProperties = analytics.management().webproperties().list(accountId).execute();
		List<Webproperty> webPropertiesList = webProperties.getItems();

		if (webPropertiesList.isEmpty()) {
			log.error("No Web Properties found");
		} else {

			for(Webproperty webProperty : webPropertiesList){
				if(webProperty.getName().equals(Util.getGoogleAnalyticsWebPropertyName())){
					webPropertyId = webProperty.getId();
				}
			}
		}

		return webPropertyId;
	}

	private static String getGAViewId(Analytics analytics, String accountId, String webPropertyId)
			throws IOException {

		String viewId = null;

		Profiles views = analytics.management().profiles().list(accountId, webPropertyId).execute();
		List<Profile> viewsList = views.getItems();

		if (viewsList.isEmpty()) {
			log.error("No profiles found");
		} else {

			for(Profile profile : viewsList){
				if(profile.getName().equals(Util.getGoogleAnalyticsViewName())){
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
			GaData gaData = analytics.data().ga().get("ga:" + viewId, startDate, endDate, "ga:users")
			                         .setDimensions("ga:" + dimension)
			                         .setMaxResults(200).execute();

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

		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		Credential credential = authorizeUser();
		Analytics analytics = getAnalyticsServiceObject(credential);
		String accountId = getGAAccountId(analytics);
		String webPropertyId = getGAWebPropertyId(analytics,accountId);
		String viewId = getGAViewId(analytics,accountId,webPropertyId);

		List<GoogleAnalyticsData> continentDataList= executeGAQuery(analytics, viewId, "continent", startDate, endDate);
		List<GoogleAnalyticsData> countryDataList = executeGAQuery(analytics, viewId, "country", startDate, endDate);

		int totalNoOfUsers=0;
		int europeNoOfUsers=0;
		int usaCanadaNoOfUsers=0;

		for(GoogleAnalyticsData continent : continentDataList){
			totalNoOfUsers = totalNoOfUsers + continent.getValue();
			if(continent.getName().equals("Europe")){
				europeNoOfUsers = continent.getValue();
			}
		}

		for(GoogleAnalyticsData country : countryDataList){
			if(country.getName().equals("Canada")){
				usaCanadaNoOfUsers = usaCanadaNoOfUsers+ country.getValue();
			}
			else if(country.getName().equals("United States")){
				usaCanadaNoOfUsers = usaCanadaNoOfUsers+ country.getValue();
			}
		}

		int rowNoofUsers= totalNoOfUsers-europeNoOfUsers-usaCanadaNoOfUsers;

		List<RegionCount> regionCountList = new ArrayList<RegionCount>();

		RegionCount regionCount = new RegionCount();
		regionCount.setRegion("total_users");
		regionCount.setNoOfUsers(totalNoOfUsers);
		regionCountList.add(regionCount);

		regionCount = new RegionCount();
		regionCount.setRegion("eu_users");
		regionCount.setNoOfUsers(europeNoOfUsers);
		regionCountList.add(regionCount);

		regionCount = new RegionCount();
		regionCount.setRegion("na_users");
		regionCount.setNoOfUsers(usaCanadaNoOfUsers);
		regionCountList.add(regionCount);

		regionCount = new RegionCount();
		regionCount.setRegion("row_users");
		regionCount.setNoOfUsers(rowNoofUsers);
		regionCountList.add(regionCount);

		regionCount = new RegionCount();
		regionCount.setRegion("unclassified_users");
		regionCount.setNoOfUsers(0);
		regionCountList.add(regionCount);

		return regionCountList;

	}

}
