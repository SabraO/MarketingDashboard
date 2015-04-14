package org.wso2.dashboard.marketing.publish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Application {

	private static Log log = LogFactory.getLog(Application.class);

	public static void main(String[] args){

		try {
			DataPublisher.publishData();
		} catch (Exception e) {
			log.warn(e);
		}

	}

}
