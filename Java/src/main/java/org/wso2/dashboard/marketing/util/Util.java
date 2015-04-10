package org.wso2.dashboard.marketing.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {

	private static Properties MAIN_PROPERTIES = new Properties();

	public static void initializeMainProperties() throws IOException {

		String path = "./main.properties";
		FileInputStream file = new FileInputStream(path);

		MAIN_PROPERTIES.load(file);

		file.close();
	}

	public static String getSalesForceAuthEndPoint(){
		return MAIN_PROPERTIES.getProperty("salesforce.authendpoint");
	}

	public static String getSalesForceUserName(){
		return MAIN_PROPERTIES.getProperty("salesforce.username");
	}

	public static String getSalesForcePassword(){
		return MAIN_PROPERTIES.getProperty("salesforce.password");
	}

	public static String getSalesForceSecurityToken(){
		return MAIN_PROPERTIES.getProperty("salesforce.securitytoken");
	}

	public static String getSalesForceURL(){
		return MAIN_PROPERTIES.getProperty("salesforce.url");
	}

	public static String getRightWaveURL(){
		return MAIN_PROPERTIES.getProperty("rightwave.url");
	}

	public static String getGoogleAnalyticsAccountName(){

		return MAIN_PROPERTIES.getProperty("googleanalytics.accountname");
	}

	public static String getGoogleAnalyticsWebPropertyName(){
		return MAIN_PROPERTIES.getProperty("googleanalytics.webpropertyname");
	}

	public static String getGoogleAnalyticsViewName(){
		return MAIN_PROPERTIES.getProperty("googleanalytics.viewname");
	}

	public static String getSQLsPerWeekReportId(){
		return MAIN_PROPERTIES.getProperty("salesforce.perweekreportid");
	}

	public static String getPartialSQLsForPreviousQuarterReportId(){
		return MAIN_PROPERTIES.getProperty("salesforce.sqlsforpreviousquarterreportid");
	}

	public static String getPartialSQLsForNextQuarterReportId(){
		return MAIN_PROPERTIES.getProperty("salesforce.sqlsfornextquarterreportid");
	}

}
