package org.wso2.dashboard.marketing.access.data;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.model.querydata.SalesForceData;
import org.wso2.dashboard.marketing.util.Constants;
import org.wso2.dashboard.marketing.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalesForceAccess {

	private static final String NA = "\"NA\"";
	private static final String EUROPE = "\"EUROPE\"";
	private static final String MEAP = "\"MEAP\"";
	private static final String SA = "\"SA\"";
	private static final String ROW = "\"ROW\"";

	private static final String AUTHENTICATION_EP = "salesforce.authendpoint";
	private static final String USERNAME = "salesforce.username";
	private static final String PASSWORD = "salesforce.password";
	private static final String SECURITY_TOKEN = "salesforce.securitytoken";

	private static final String URL = "salesforce.url";
	private static final String URL_EXTENSION = "?export=1&enc=UTF-8&xf=csv";
	private static final String SESSION_ID_URL_PARAMETER = "sid=";
	private static final String REQUEST_METHOD_TYPE = "GET";
	private static final String REQUEST_COOKIE_HEADER = "Cookie";

	private static final String SEPARATOR = ",";

	private static ConnectorConfig createEnterpriseConnection() throws ConnectionException {

		String authEndPoint = Util.getProperty(AUTHENTICATION_EP);
		String username = Util.getProperty(USERNAME);
		String password = Util.getProperty(PASSWORD) + Util.getProperty(SECURITY_TOKEN);

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		config.setAuthEndpoint(authEndPoint);
		new EnterpriseConnection(config);
		return config;
	}

	private static String getSessionId(ConnectorConfig config) {
		return config.getSessionId();
	}

	private static HttpURLConnection createHttpUrlConnection(String reportId) throws IOException {

		String url = Util.getProperty(URL) + reportId + URL_EXTENSION;
		URL myUrl = new URL(url);

		return (HttpURLConnection) myUrl.openConnection();
	}

	private static void configureHttpUrlConnection(HttpURLConnection urlConn, String sessionId) throws IOException {

		String myCookie = SESSION_ID_URL_PARAMETER + sessionId;
		urlConn.setRequestMethod(REQUEST_METHOD_TYPE);
		urlConn.setRequestProperty(REQUEST_COOKIE_HEADER, myCookie);
		urlConn.setRequestProperty(Constants.USER_AGENT_HEADER, Constants.USER_AGENT);

	}

	private static List<SalesForceData> getRegionList(URLConnection urlConn) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		String inputLine;

		List<SalesForceData> regionList = new ArrayList<SalesForceData>();
		SalesForceData data;

		while ((inputLine = in.readLine()) != null) {

			data = new SalesForceData();
			data.setRegion(inputLine.split(SEPARATOR)[0]);
			regionList.add(data);

		}
		in.close();

		return regionList;
	}

	public static List<RegionCount> getNoOfUsersPerRegion(String reportId) throws ConnectionException, IOException {

		List<SalesForceData> regionList = getSalesForceData(reportId);
		List<Integer> userCountList = calculateNoOfUsersPerRegion(regionList);

		List<RegionCount> regionCountList = new ArrayList<RegionCount>();

		addRegionData(Constants.TOTAL, userCountList.get(0), regionCountList);
		addRegionData(Constants.EU, userCountList.get(1), regionCountList);
		addRegionData(Constants.NA, userCountList.get(2), regionCountList);
		addRegionData(Constants.ROW, userCountList.get(3), regionCountList);
		addRegionData(Constants.UNCLASSIFIED, userCountList.get(4), regionCountList);

		return regionCountList;
	}

	private static List<Integer> calculateNoOfUsersPerRegion(List<SalesForceData> regionList) {

		int europeNoOfUsers = 0, usaCanadaNoOfUsers = 0, rowNoOfUsers = 0, unclassifiedNoOfUsers = 0, totalNoOfUsers;
		String region;

		for (SalesForceData regionData : regionList) {
			region = regionData.getRegion();

			if (NA.equals(region)) {
				usaCanadaNoOfUsers++;
			} else if (EUROPE.equals(region)) {
				europeNoOfUsers++;
			} else if (ROW.equals(region) || MEAP.equals(region) || SA.equals(region)) {
				rowNoOfUsers++;
			}
		}

		totalNoOfUsers = rowNoOfUsers + europeNoOfUsers + usaCanadaNoOfUsers;

		return Arrays.asList(totalNoOfUsers, europeNoOfUsers, usaCanadaNoOfUsers, rowNoOfUsers, unclassifiedNoOfUsers);
	}

	private static List<SalesForceData> getSalesForceData(String reportId) throws ConnectionException, IOException {

		ConnectorConfig config = createEnterpriseConnection();
		String sessionId = getSessionId(config);
		HttpURLConnection urlConn = createHttpUrlConnection(reportId);
		configureHttpUrlConnection(urlConn, sessionId);
		return getRegionList(urlConn);

	}

	private static void addRegionData(String region, int noOfUsers, List<RegionCount> dataList) {
		RegionCount regionCount = new RegionCount();
		regionCount.setRegion(region);
		regionCount.setNoOfUsers(noOfUsers);
		dataList.add(regionCount);
	}
}
