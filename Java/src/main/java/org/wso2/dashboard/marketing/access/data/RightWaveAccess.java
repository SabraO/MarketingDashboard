package org.wso2.dashboard.marketing.access.data;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.model.querydata.RightWaveResponseData;
import org.wso2.dashboard.marketing.util.Constants;
import org.wso2.dashboard.marketing.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RightWaveAccess {

	private static final String URL = "rightwave.url";
	private static final String REQUEST_TYPE = "json";

	private static HttpPost createHttpPostRequest() throws IOException {

		String url = Util.getProperty(URL);
		return new HttpPost(url);
	}

	private static void configureHttpPostRequest(HttpPost post,String startDate, String endDate)
																				throws UnsupportedEncodingException {
		post.setHeader(Constants.USER_AGENT_HEADER, Constants.USER_AGENT);

		List<org.apache.http.NameValuePair> urlParameters = new ArrayList<org.apache.http.NameValuePair>();
		urlParameters.add(new BasicNameValuePair(REQUEST_TYPE, "{ \"startdate\": \"" + startDate + "\", \"enddate\":\"" +
		                                                                                            endDate + "\" }"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
	}

	private static HttpResponse executePostRequest(HttpPost post) throws IOException {

		HttpClient client = new DefaultHttpClient();
		return client.execute(post);
	}

	public static List<RegionCount> getNoOfUsersPerRegion(String startDate, String endDate) throws IOException {

		StringBuilder result = getRightWaveJSONResponse(startDate, endDate);
		RightWaveResponseData responseData = getRightWaveJSONObject(result);
		List<Integer> userCountList = calculateNoOfUsersPerRegion(responseData);

		List<RegionCount> regionCountList = new ArrayList<RegionCount>();

		addRegionData(Constants.TOTAL,userCountList.get(0), regionCountList);
		addRegionData(Constants.EU,userCountList.get(1),regionCountList);
		addRegionData(Constants.NA,userCountList.get(2), regionCountList);
		addRegionData(Constants.ROW, userCountList.get(3), regionCountList);
		addRegionData(Constants.UNCLASSIFIED, userCountList.get(4), regionCountList);

		return regionCountList;
	}

	private static List<Integer> calculateNoOfUsersPerRegion(RightWaveResponseData responseData){
		int europeNoOfUsers= responseData.getOutput().getEu();
		int usaCanadaNoOfUsers= responseData.getOutput().getNa();
		int rowNoOfUsers= responseData.getOutput().getRow();
		int unclassifiedNoOfUsers = responseData.getOutput().getUnclassified();

		int totalNoOfUsers = rowNoOfUsers + europeNoOfUsers + usaCanadaNoOfUsers + unclassifiedNoOfUsers;

		return Arrays.asList(totalNoOfUsers, europeNoOfUsers, usaCanadaNoOfUsers, rowNoOfUsers, unclassifiedNoOfUsers);
	}

	private static StringBuilder getRightWaveJSONResponse(String startDate, String endDate) throws IOException {

		HttpPost post = createHttpPostRequest();
		configureHttpPostRequest(post,startDate,endDate);
		HttpResponse response = executePostRequest(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result;
	}

	private static RightWaveResponseData getRightWaveJSONObject(StringBuilder result) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(result.toString(), RightWaveResponseData.class);

	}

	private static void addRegionData(String region, int noOfUsers, List<RegionCount> regionCountList) {
		RegionCount regionCount = new RegionCount();
		regionCount.setRegion(region);
		regionCount.setNoOfUsers(noOfUsers);
		regionCountList.add(regionCount);
	}
}
