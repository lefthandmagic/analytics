package com.uber.analytics.job;

import com.uber.request.AnalyticsRequest;

/**
 * Analytics Job Interface. This interface has various implementations capable
 * of processing various analytics requests.
 * 
 * @author pmurugesan
 * 
 */
public interface AnalyticsJob {

	/**
	 * Handle the analytics job request
	 * 
	 * @param request
	 */
	void handle(AnalyticsRequest request);

}
