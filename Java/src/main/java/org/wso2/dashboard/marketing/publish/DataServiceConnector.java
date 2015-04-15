package org.wso2.dashboard.marketing.publish;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.wso2.dashboard.marketing.client.WSO2MarketingDashboardDataServiceStub;
import org.wso2.dashboard.marketing.util.Util;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DataServiceConnector {

	private static final String DATASERVICE_EPR = "dataservice.epr";
	private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
	private static final String TRUST_STORE_PATH = "truststore.path";
	private static final String TRUST_STORE_PASSWORD_PROPERTY = "javax.net.ssl.trustStorePassword";
	private static final String TRUST_STORE_PASSWORD = "wso2carbon";
	private static final String LOCAL_REPO_PATH = "localrepo.path";
	private static final String MODULE = "rampart";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String POLICY_FILE_PATH = "policyfile.path";

	public static WSO2MarketingDashboardDataServiceStub createConnection()
			throws AxisFault, FileNotFoundException, XMLStreamException {

		String epr = Util.getProperty(DATASERVICE_EPR);

		System.setProperty(TRUST_STORE_PROPERTY, (new File(Util.getProperty(TRUST_STORE_PATH))).getAbsolutePath());
		System.setProperty(TRUST_STORE_PASSWORD_PROPERTY, TRUST_STORE_PASSWORD);

		ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(Util.getProperty(LOCAL_REPO_PATH), null);
		WSO2MarketingDashboardDataServiceStub stub = new WSO2MarketingDashboardDataServiceStub(ctx, epr);
		ServiceClient client = stub._getServiceClient();
		Options options = client.getOptions();
		client.engageModule(MODULE);
		options.setUserName(USERNAME);
		options.setPassword(PASSWORD);

		options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy(Util.getProperty(POLICY_FILE_PATH)));

		return stub;

	}

	private static Policy loadPolicy(String path) throws FileNotFoundException, XMLStreamException {
		InputStream resource = new FileInputStream(path);
		StAXOMBuilder builder = new StAXOMBuilder(resource);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}
}
