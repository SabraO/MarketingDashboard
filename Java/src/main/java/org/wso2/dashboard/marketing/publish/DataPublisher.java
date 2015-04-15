package org.wso2.dashboard.marketing.publish;

import com.sforce.ws.ConnectionException;
import org.wso2.dashboard.marketing.access.data.GoogleAnalyticsAccess;
import org.wso2.dashboard.marketing.access.data.RightWaveAccess;
import org.wso2.dashboard.marketing.access.data.SalesForceAccess;
import org.wso2.dashboard.marketing.client.WSO2MarketingDashboardDataServiceStub;
import org.wso2.dashboard.marketing.model.DateRange;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;
import org.wso2.dashboard.marketing.publish.data.RawLeadsPersist;
import org.wso2.dashboard.marketing.publish.data.SalesQualifiedLeadsPersist;
import org.wso2.dashboard.marketing.publish.data.WebsiteVisitorsPersist;
import org.wso2.dashboard.marketing.util.DateFunction;
import org.wso2.dashboard.marketing.util.Util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.List;

public class DataPublisher {

	private static List<RegionCount> websiteVisitors, rawLeads, salesQualifiedLeads;
	private static WebsiteVisitorsPersist wvPersist;
	private static RawLeadsPersist rlPersist;
	private static SalesQualifiedLeadsPersist sqlPersist;
	private static WSO2MarketingDashboardDataServiceStub stub;

	private static final String WEEKLY_REPORT_ID = "salesforce.perweekreportid";
	private static final String PREV_QUARTER_REPORT_ID = "salesforce.sqlsforpreviousquarterreportid";
	private static final String NEXT_QUARTER_REPORT_ID = "salesforce.sqlsfornextquarterreportid";

	private static void initialize() throws Exception {

		Util.initializeMainProperties();
		stub = DataServiceConnector.createConnection();
		wvPersist = new WebsiteVisitorsPersist();
		rlPersist = new RawLeadsPersist();
		sqlPersist = new SalesQualifiedLeadsPersist();

	}

	public static void publishData() throws Exception {

		initialize();

		//Save weekly data
		DateRange dateRange = DateFunction.getWeeklyDateRange();
		String startDate = dateRange.getStartDate(), endDate = dateRange.getEndDate();
		String reportId = Util.getProperty(WEEKLY_REPORT_ID);//SQLsPerWeek Report

		setLeadData(startDate, endDate, reportId);
		saveWeeklyData(startDate, endDate);

		//Check if the date range contains the end of quarter(EOQ)
		int quarter = DateFunction.getQNumIfDateRangeContainsEOQ(startDate, endDate);

		if (quarter != -1) {//Proceed if the date range contains the EOQ
			int year = Calendar.getInstance().get(Calendar.YEAR);
			DateRange quarterDateRange;
			reportId = Util.getProperty(PREV_QUARTER_REPORT_ID);//PartialSQLsForPreviousQuarter Report
			String startDateQ, endDateQ;

			List<DateRange> dateRangeList = DateFunction.getQuarterlyDateRange(startDate, endDate, quarter);

			for (int i = 0; i < dateRangeList.size(); i++) {

				quarterDateRange = dateRangeList.get(i);
				startDateQ = quarterDateRange.getStartDate();
				endDateQ = quarterDateRange.getEndDate();

				if (i == 1) {
					quarter = quarter + 1;
					reportId = Util.getProperty(NEXT_QUARTER_REPORT_ID);//PartialSQLsForNextQuarter Report

					setLeadData(startDateQ, endDateQ, reportId);

					if (quarter == 5) {
						saveQuarterlyData(1, year + 1);
						saveYearlyData(year);
					} else {
						saveQuarterlyData(quarter, year);
					}

				} else {

					setLeadData(startDateQ, endDateQ, reportId);
					saveQuarterlyData(quarter, year);
				}

			}
		}
	}

	private static void setLeadData(String startDate, String endDate, String reportId)
			throws IOException, GeneralSecurityException, ConnectionException {
		//rawLeads = RightWaveAccess.getNoOfUsersPerRegion(startDate, endDate);
		websiteVisitors = GoogleAnalyticsAccess.getNoOfUsersPerRegion(startDate, endDate);
		//salesQualifiedLeads = SalesForceAccess.getNoOfUsersPerRegion(reportId);
	}

	private static void saveYearlyData(int year) throws RemoteException {
		wvPersist.insertToYearlyDB(stub, Integer.toString(year + 1), websiteVisitors);
		rlPersist.insertToYearlyDB(stub, Integer.toString(year + 1), rawLeads);
		sqlPersist.insertToYearlyDB(stub, Integer.toString(year + 1), salesQualifiedLeads);
	}

	private static void saveQuarterlyData(int quarter, int year) throws RemoteException {
		wvPersist.insertToQuarterlyDB(stub, Integer.toString(year), quarter, websiteVisitors);
		rlPersist.insertToQuarterlyDB(stub, Integer.toString(year), quarter, rawLeads);
		sqlPersist.insertToQuarterlyDB(stub, Integer.toString(year), quarter, salesQualifiedLeads);
	}

	private static void saveWeeklyData(String startDate, String endDate) throws RemoteException {
		wvPersist.insertToWeeklyDB(stub, startDate, endDate, websiteVisitors);
		//rlPersist.insertToWeeklyDB(stub, startDate, endDate, rawLeads);
		//sqlPersist.insertToWeeklyDB(stub, startDate, endDate, salesQualifiedLeads);
	}

}
