package org.wso2.dashboard.marketing.publish.data;

import com.sforce.ws.ConnectionException;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.wso2.dashboard.marketing.access.data.GoogleAnalyticsAccess;
import org.wso2.dashboard.marketing.access.data.RightWaveAccess;
import org.wso2.dashboard.marketing.access.data.SalesForceAccess;
import org.wso2.dashboard.marketing.client.WSO2MarketingDashboardDataServiceStub;
import org.wso2.dashboard.marketing.client.WSO2MarketingDashboardDataServiceStub.*;
import org.wso2.dashboard.marketing.model.DateRange;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataPublisher {

	public static void publishData() throws Exception {

		Util.initializeMainProperties();

		//Save weekly data
		DateRange dateRange = getWeeklyDateRange();
		WSO2MarketingDashboardDataServiceStub stub = createConnection();
		String startDate = dateRange.getStartDate(), endDate = dateRange.getEndDate();
		String reportId = Util.getSQLsPerWeekReportId();//SQLsPerWeek Report

		//WV Weekly
		List<RegionCount> websiteVisitors = GoogleAnalyticsAccess.getNoOfUsersPerRegion(startDate, endDate);
		insertToWebsiteVisitorsWeeklyDB(stub, startDate, endDate, websiteVisitors);

		//RL Weekly
		List<RegionCount> rawLeads = RightWaveAccess.getNoOfUsersPerRegion(startDate, endDate);
		insertToRawLeadsWeeklyDB(stub, startDate, endDate, rawLeads);

		//SQL Weekly
		List<RegionCount> salesQualifiedLeads = SalesForceAccess.getNoOfUsersPerRegion(reportId);
		insertToSalesQualifiedLeadsWeeklyDB(stub, startDate, endDate, salesQualifiedLeads);

		//Save quarterly data
		int firstQuarterNum = getQNumIfDateRangeContainsEOQ(startDate, endDate), quarter = firstQuarterNum;
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		DateRange quarterDateRange;
		reportId = Util.getPartialSQLsForPreviousQuarterReportId();//PartialSQLsForPreviousQuarter Report
		String startDateQ, endDateQ;

		if (firstQuarterNum != -1) {
			List<DateRange> dateRangeList = getQuarterlyDateRange(startDate, endDate, firstQuarterNum);

			for (int i = 0; i < dateRangeList.size(); i++) {

				quarterDateRange = dateRangeList.get(i);
				startDateQ = quarterDateRange.getStartDate();
				endDateQ = quarterDateRange.getEndDate();

				if (i == 1) {
					quarter = quarter + 1;
					reportId = Util.getPartialSQLsForNextQuarterReportId();//PartialSQLsForNextQuarter Report

					if (quarter == 5) {
						//RL Quarterly
						rawLeads = RightWaveAccess.getNoOfUsersPerRegion(startDateQ, endDateQ);
						insertToRawLeadsQuarterlyDB(stub, Integer.toString(year + 1), 1, rawLeads);
						//RL Yearly
						insertToRawLeadsYearlyDB(stub,Integer.toString(year + 1),rawLeads);

						//SQL Quarterly
						salesQualifiedLeads = SalesForceAccess.getNoOfUsersPerRegion(reportId);
						insertToSalesQualifiedLeadsQuarterlyDB(stub, Integer.toString(year + 1), 1,salesQualifiedLeads);
						//SQL Yearly
						insertToSalesQualifiedLeadsYearlyDB(stub,Integer.toString(year + 1),salesQualifiedLeads);

						//WV Quarterly
						websiteVisitors = GoogleAnalyticsAccess.getNoOfUsersPerRegion(startDateQ, endDateQ);
						insertToWebsiteVisitorsQuarterlyDB(stub, Integer.toString(year + 1), 1, websiteVisitors);
						//WV Yearly
						insertToWebsiteVisitorsYearlyDB(stub,Integer.toString(year + 1),websiteVisitors);

					}
					else {
						saveQuarterlyData(stub,startDateQ,endDateQ,year,quarter,reportId);
					}

				} else {
					saveQuarterlyData(stub,startDateQ,endDateQ,year,quarter,reportId);
				}

			}
		}
	}

	public static void saveQuarterlyData(WSO2MarketingDashboardDataServiceStub stub,String startDate,String endDate,
	                                                                            int year, int quarter,String reportId)
			throws IOException, ConnectionException, GeneralSecurityException {
		//RL Quarterly
		List<RegionCount> rawLeads = RightWaveAccess.getNoOfUsersPerRegion(startDate, endDate);
		insertToRawLeadsQuarterlyDB(stub, Integer.toString(year), quarter, rawLeads);

		//SQL Quarterly
		List<RegionCount> salesQualifiedLeads = SalesForceAccess.getNoOfUsersPerRegion(reportId);
		insertToSalesQualifiedLeadsQuarterlyDB(stub, Integer.toString(year), quarter, salesQualifiedLeads);

		//WV Quarterly
		List<RegionCount> websiteVisitors = GoogleAnalyticsAccess.getNoOfUsersPerRegion(startDate, endDate);
		insertToWebsiteVisitorsQuarterlyDB(stub, Integer.toString(year), quarter, websiteVisitors);
	}

	private static void insertToRawLeadsYearlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                                             List<RegionCount> regionCountList) throws RemoteException {

		InsertRawLeadsPerYear request = new InsertRawLeadsPerYear();

		request.setYear(year);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertRawLeadsPerYear(request);

	}

	private static void insertToSalesQualifiedLeadsYearlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                                                        List<RegionCount> regionCountList) throws RemoteException {

		InsertSalesQualifiedLeadsPerYear request = new InsertSalesQualifiedLeadsPerYear();

		request.setYear(year);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertSalesQualifiedLeadsPerYear(request);

	}

	private static void insertToWebsiteVisitorsYearlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                                                    List<RegionCount> regionCountList) throws RemoteException {

		InsertWebsiteVisitorsPerYear request = new InsertWebsiteVisitorsPerYear();

		request.setYear(year);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertWebsiteVisitorsPerYear(request);
	}

	private static void insertToRawLeadsQuarterlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,int quarter,
	                                                List<RegionCount> regionCountList) throws RemoteException {

		InsertRawLeadsPerQuarter request = new InsertRawLeadsPerQuarter();

		request.setYear(year);
		request.setQuarter(quarter);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertRawLeadsPerQuarter(request);
	}

	private static void insertToSalesQualifiedLeadsQuarterlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                                            int quarter, List<RegionCount> regionCountList) throws RemoteException {

		InsertSalesQualifiedLeadsPerQuarter request = new InsertSalesQualifiedLeadsPerQuarter();

		request.setYear(year);
		request.setQuarter(quarter);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertSalesQualifiedLeadsPerQuarter(request);

	}

	private static void insertToWebsiteVisitorsQuarterlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                                    int quarter, List<RegionCount> regionCountList) throws RemoteException {

		InsertWebsiteVisitorsPerQuarter request = new InsertWebsiteVisitorsPerQuarter();

		request.setYear(year);
		request.setQuarter(quarter);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertWebsiteVisitorsPerQuarter(request);
	}

	private static DateRange getWeeklyDateRange(){

		DateRange weeklyDateRange = new DateRange();

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

		long DAY_IN_MS = 1000 * 60 * 60 * 24;

		//Get first date in date range
		Date firstDate = new Date(System.currentTimeMillis() - (8 * DAY_IN_MS));
		weeklyDateRange.setStartDate(sdfDate.format(firstDate));

		//Get last date in date range
		Date lastDate = new Date(System.currentTimeMillis() - (2 * DAY_IN_MS));
		weeklyDateRange.setEndDate(sdfDate.format(lastDate));

		return weeklyDateRange;
	}

	private static List<DateRange> getQuarterlyDateRange(String startDateString, String endDateString, int quarterNum)
			throws ParseException {

		List<DateRange> dateRangesList = new ArrayList<DateRange>();

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String quarterEndDateString = Integer.toString(year);

		switch (quarterNum){
			case 1: quarterEndDateString += "-03-31"; break;
			case 2: quarterEndDateString += "-06-30"; break;
			case 3: quarterEndDateString += "-09-30"; break;
			case 4: quarterEndDateString += "-12-31"; break;
		}

		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Date quarterDate = sdfDate.parse(quarterEndDateString);
		Date dateAfterQuarter = new Date(quarterDate.getTime() + DAY_IN_MS);

		DateRange firstDateRange = new DateRange();
		firstDateRange.setStartDate(startDateString);
		firstDateRange.setEndDate(sdfDate.format(quarterDate));
		dateRangesList.add(firstDateRange);

		DateRange secondDateRange = new DateRange();
		secondDateRange.setStartDate(sdfDate.format(dateAfterQuarter));
		secondDateRange.setEndDate(endDateString);
		dateRangesList.add(secondDateRange);

		return dateRangesList;
	}

	private static int getQNumIfDateRangeContainsEOQ(String startDateString,String endDateString)throws ParseException{

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);

		//Check for all the quarter end dates
		String[] quarterEndDates = {"03-31", "06-30", "09-30", "12-31"};

		String quarterEndDateString = Integer.toString(year) + "-", eoqDateString;
		Date startDate = sdfDate.parse(startDateString), endDate = sdfDate.parse(endDateString), quarterDate;

		for(int i =0 ; i< quarterEndDates.length; i++){

			eoqDateString = quarterEndDates[i];
			quarterEndDateString += eoqDateString;
			quarterDate = sdfDate.parse(quarterEndDateString);

			if(quarterDate.after(startDate)&&quarterDate.before(endDate)){
				return i + 1;
			}

		}

		return -1;
	}

	private static void insertToRawLeadsWeeklyDB(WSO2MarketingDashboardDataServiceStub stub,String startDate,
	                                                String endDate, List<RegionCount> regionCountList)
														throws RemoteException {

		InsertRawLeadsPerWeek request = new InsertRawLeadsPerWeek();

		request.setStart_date(startDate);
		request.setEnd_date(endDate);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertRawLeadsPerWeek(request);

	}

	private static void insertToSalesQualifiedLeadsWeeklyDB(WSO2MarketingDashboardDataServiceStub stub,String startDate,
	                                             String endDate, List<RegionCount> regionCountList)
														throws RemoteException {

		InsertSalesQualifiedLeadsPerWeek request = new InsertSalesQualifiedLeadsPerWeek();

		request.setStart_date(startDate);
		request.setEnd_date(endDate);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertSalesQualifiedLeadsPerWeek(request);

	}

	private static void insertToWebsiteVisitorsWeeklyDB(WSO2MarketingDashboardDataServiceStub stub,String startDate,
	                                             String endDate, List<RegionCount> regionCountList)
														throws RemoteException {

		InsertWebsiteVisitorsPerWeek request = new InsertWebsiteVisitorsPerWeek();

		request.setStart_date(startDate);
		request.setEnd_date(endDate);
		request.setTotal_users(regionCountList.get(0).getNoOfUsers());
		request.setEu_users(regionCountList.get(1).getNoOfUsers());
		request.setNa_users(regionCountList.get(2).getNoOfUsers());
		request.setRow_users(regionCountList.get(3).getNoOfUsers());
		request.setUnclassified_users(regionCountList.get(4).getNoOfUsers());

		stub.insertWebsiteVisitorsPerWeek(request);

	}

	private static WSO2MarketingDashboardDataServiceStub createConnection() throws Exception {

		String epr = "https://localhost:9443/services/WSO2MarketingDashboardDataService";
		System.setProperty("javax.net.ssl.trustStore", (new File("./config/wso2carbon.jks")).getAbsolutePath());
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

		ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem("/home/sabra/repository", null);
		WSO2MarketingDashboardDataServiceStub stub = new WSO2MarketingDashboardDataServiceStub(ctx,epr);
		ServiceClient client = stub._getServiceClient();
		Options options = client.getOptions();
		client.engageModule("rampart");
		options.setUserName("admin");
		options.setPassword("admin");
		
		options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy("./config/wso2MDPolicy.xml"));

		return stub;

	}

	private static Policy loadPolicy(String path) throws Exception {
		InputStream resource = new FileInputStream(path);
		StAXOMBuilder builder = new StAXOMBuilder(resource);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

}
