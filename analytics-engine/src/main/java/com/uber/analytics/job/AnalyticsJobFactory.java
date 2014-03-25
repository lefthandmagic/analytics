package com.uber.analytics.job;

import java.util.logging.Logger;
import com.uber.request.AnalyticsRequestType;

/**
 * Analytics Job Factory object
 * 
 * @author pmurugesan
 * 
 */
public class AnalyticsJobFactory {

	// logger
	private static final Logger LOGGER = Logger
			.getLogger(AnalyticsJobFactory.class.getName());

	/**
	 * Creates an analytics job instance for the incoming request type
	 * 
	 * @param type
	 * @return
	 */
	public static AnalyticsJob createAnalyticsJob(AnalyticsRequestType type) {
		try {
			return type.getClassRef().newInstance();
		} catch (InstantiationException e) {
			LOGGER.severe("Instantiation exception when instantiating analytics job: "
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			LOGGER.severe("Illegal argument exception when instantiating analytics job: "
					+ e.getMessage());
		}
		return null;
	}

}
