package org.wso2.dashboard.marketing.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {

	private static final String PROPERTY_FILE_PATH = "../main.properties";
	private static final Properties MAIN_PROPERTIES = new Properties();

	public static void initializeMainProperties() throws IOException {

		FileInputStream file = new FileInputStream(PROPERTY_FILE_PATH);

		MAIN_PROPERTIES.load(file);

		file.close();
	}

	public static String getProperty(String key) {
		return MAIN_PROPERTIES.getProperty(key);
	}
}
