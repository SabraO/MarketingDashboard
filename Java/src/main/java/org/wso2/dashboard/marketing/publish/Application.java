package org.wso2.dashboard.marketing.publish;

import org.apache.log4j.Logger;

public class Application {

	private static final Logger log = Logger.getLogger(Application.class);

	public static void main(String[] args) {

		try {
			DataPublisher.publishData();
		} catch (Exception e) {
			log.error(e);
		}

	}

}
