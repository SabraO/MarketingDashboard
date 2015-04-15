package org.wso2.dashboard.marketing.publish.data;

import org.wso2.dashboard.marketing.client.WSO2MarketingDashboardDataServiceStub;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;

import java.rmi.RemoteException;
import java.util.List;

public interface Leads {

	public void insertToWeeklyDB(WSO2MarketingDashboardDataServiceStub stub, String startDate, String endDate,
	                             List<RegionCount> regionCountList) throws RemoteException;

	public void insertToQuarterlyDB(WSO2MarketingDashboardDataServiceStub stub, String year, int quarter,
	                                List<RegionCount> regionCountList) throws RemoteException;

	public void insertToYearlyDB(WSO2MarketingDashboardDataServiceStub stub, String year,
	                             List<RegionCount> regionCountList) throws RemoteException;
}
