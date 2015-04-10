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
import org.wso2.dashboard.marketing.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RightWaveAccess {

	private static HttpPost createHttpPostRequest() throws IOException {

		String url = Util.getRightWaveURL();
		return new HttpPost(url);
	}

	private static void configureHttpPostRequest(HttpPost post,String startDate, String endDate)
																				throws UnsupportedEncodingException {
		post.setHeader("User-Agent", "Chrome/4.0.249.0");

		List<org.apache.http.NameValuePair> urlParameters = new ArrayList<org.apache.http.NameValuePair>();
		urlParameters.add(new BasicNameValuePair("json", "{ \"startdate\": \"" + startDate + "\", \"enddate\":\"" +
		                                                                                            endDate + "\" }"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
	}

	private static HttpResponse executePostRequest(HttpPost post) throws IOException {

		HttpClient client = new DefaultHttpClient();
		return client.execute(post);
	}

	public static List<RegionCount> getNoOfUsersPerRegion(String startDate, String endDate) throws IOException {


		HttpPost post = createHttpPostRequest();
		configureHttpPostRequest(post,startDate,endDate);
		HttpResponse response = executePostRequest(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		RightWaveResponseData responseData = mapper.readValue(result.toString(), RightWaveResponseData.class);

		int europeNoOfUsers= responseData.getOutput().getEu();
		int usaCanadaNoOfUsers= responseData.getOutput().getNa();
		int rowNoofUsers= responseData.getOutput().getRow();
		int unclassifiedUsers = responseData.getOutput().getUnclassified();

		int totalNoOfUsers = rowNoofUsers + europeNoOfUsers + usaCanadaNoOfUsers + unclassifiedUsers;

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
		regionCount.setNoOfUsers(unclassifiedUsers);
		regionCountList.add(regionCount);

		return regionCountList;
	}
}
