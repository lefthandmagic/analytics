package com.uber.analytics.job;

import java.util.Date;
import java.util.SortedSet;

import com.uber.data.DataManager;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;

/**
 * Async cleanup job to clear dates older than last hour from sorted set
 * 
 * @author pmurugesan
 * 
 */
public class LastHourListAsyncCleanupJob implements AnalyticsJob {

	/**
	 * Handle the cleanup job
	 */
	public void handle(AnalyticsRequest request) {
		SortedSet<Date> lasthourtrips = DataManager.getInstance()
				.getLastHourTrips();
		Date oneHourBack = UberDateUtils.anHourBack();
		// iterate over dates, and if older than 1 hour remove it
		for (Date date : lasthourtrips) {
			if (date.before(oneHourBack)) {
				lasthourtrips.remove(date);
			} else {
				break;
			}
		}
	}

}
