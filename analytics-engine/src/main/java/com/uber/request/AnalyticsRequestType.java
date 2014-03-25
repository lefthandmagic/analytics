package com.uber.request;

import com.uber.analytics.job.AnalyticsJob;
import com.uber.analytics.job.CityStatJob;
import com.uber.analytics.job.ClientCountStatJob;
import com.uber.analytics.job.ClientMilesStatJob;
import com.uber.analytics.job.DriverStatJob;
import com.uber.analytics.job.LastHourListAsyncCleanupJob;
import com.uber.analytics.job.TotalTripStatJob;
import com.uber.analytics.job.TripIngestJob;

/**
 * Enum to define various analtyics request types and their mapping job classes
 * @author pmurugesan
 *
 */
public enum AnalyticsRequestType {
	
	INGEST(TripIngestJob.class),
	TOTAL_TRIPS(TotalTripStatJob.class),
	TOTAL_CLIENTS(ClientCountStatJob.class),
	TOTAL_TRIPS_BY_HOUR(TotalTripStatJob.class),
	TOTAL_MILES_BY_CLIENT(ClientMilesStatJob.class),
	AVG_FARE_BY_CITY(CityStatJob.class),
	MEDIAN_RATING_FOR_DRIVER(DriverStatJob.class),
	CLEANUP_LAST_HOUR_LIST(LastHourListAsyncCleanupJob.class);
	
	// constructor
	AnalyticsRequestType(Class<? extends AnalyticsJob> classRef) {
		this.classRef = classRef;
	}
	
	// class ref variable
	private final Class<? extends AnalyticsJob> classRef;
	
	// accessor
	public Class<? extends AnalyticsJob> getClassRef() {
		return classRef;
	}

}
