package org.wso2.dashboard.marketing.access.data;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.model.querydata.SalesForceData;
import org.wso2.dashboard.marketing.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SalesForceAccess {

	private static ConnectorConfig createEnterpriseConnection() throws ConnectionException {

		String authEndPoint = Util.getSalesForceAuthEndPoint();
		String username = Util.getSalesForceUserName();
		String password = Util.getSalesForcePassword() + Util.getSalesForceSecurityToken();

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

		String url = Util.getSalesForceURL() + reportId + "?export=1&enc=UTF-8&xf=csv";
		URL myUrl = new URL(url);

		return (HttpURLConnection) myUrl.openConnection();
	}

	private static void configureHttpUrlConnection(HttpURLConnection urlConn, String sessionId) throws IOException {

		String myCookie = "sid=" + sessionId;
		urlConn.setRequestMethod("GET");
		urlConn.setRequestProperty("Cookie", myCookie);
		urlConn.setRequestProperty("User-Agent", "Chrome/4.0.249.0");

	}

	private static List<SalesForceData> getRegionList(URLConnection urlConn) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		String inputLine;

		List<SalesForceData> regionList = new ArrayList<SalesForceData>();
		SalesForceData data;

		while ((inputLine = in.readLine()) != null) {

			data = new SalesForceData();
			data.setRegion(inputLine.split(",")[0]);
			regionList.add(data);

		}
		in.close();

		return regionList;
	}

	public static List<RegionCount> getNoOfUsersPerRegion(String reportId) throws ConnectionException, IOException {

		ConnectorConfig config = createEnterpriseConnection();
		String sessionId = getSessionId(config);
		HttpURLConnection urlConn = createHttpUrlConnection(reportId);
		configureHttpUrlConnection(urlConn, sessionId);
		List<SalesForceData> regionList = getRegionList(urlConn);

		int europeNoOfUsers = 0;
		int usaCanadaNoOfUsers = 0;
		int rowNoofUsers = 0;
		String region;

		for (SalesForceData regionData : regionList) {
			region = regionData.getRegion();

			if (region.equals("\"NA\"")) {
				usaCanadaNoOfUsers++;
			} else if (region.equals("\"EUROPE\"")) {
				europeNoOfUsers++;
			} else if (region.equals("\"ROW\"") || region.equals("\"MEAP\"") || region.equals("\"SA\"")) {
				rowNoofUsers++;
			}
		}

		int totalNoOfUsers = rowNoofUsers + europeNoOfUsers + usaCanadaNoOfUsers;

		List<RegionCount> regionCountList = new ArrayList<RegionCount>();

		add(totalNoOfUsers, regionCountList);
		RegionCount regionCount;

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

	private static void add(int totalNoOfUsers, List<RegionCount> regionCountList) {
		RegionCount regionCount = new RegionCount();
		regionCount.setRegion("total_users");
		regionCount.setNoOfUsers(totalNoOfUsers);
		regionCountList.add(regionCount);
	}
}
