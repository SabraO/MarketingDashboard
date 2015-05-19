package org.wso2.dashboard.marketing.publish.data;

import org.wso2.dashboard.marketing.client.Dataservice1DefaultSNAPSHOTStub;
import org.wso2.dashboard.marketing.model.processeddata.RegionCount;

import java.rmi.RemoteException;
import java.util.List;

public interface Leads {

	public void insertToWeeklyDB(Dataservice1DefaultSNAPSHOTStub stub, String startDate, String endDate,
	                             List<RegionCount> regionCountList) throws RemoteException;

	public void insertToQuarterlyDB(Dataservice1DefaultSNAPSHOTStub stub, String year, int quarter,
	                                List<RegionCount> regionCountList) throws RemoteException;

	public void insertToYearlyDB(Dataservice1DefaultSNAPSHOTStub stub, String year,
	                             List<RegionCount> regionCountList) throws RemoteException;
}
