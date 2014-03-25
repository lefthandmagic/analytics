package com.uber.analytics.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.uber.data.DataManager;
import com.uber.request.AnalyticsRequest;
import com.uber.request.AnalyticsRequestType;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Server resource which handles driver stats
 * @author pmurugesan
 *
 */
public class DriverManager extends ServerResource {
	
	// driver id
	private String driverId;
	private static final Logger LOGGER = Logger.getLogger(DriverManager.class
			.getName());
	
	/**
	 * Initialize from attributes
	 */
	@Override
	protected void doInit()  {
		this.driverId = getAttribute(RequestParams.DRIVER_ID);
	}
	
	/**
	 * Get the median rating for driver
	 */
	@Get
	public void driverRequest() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();	
			params.put(RequestParams.DRIVER_ID, this.driverId);
			DataManager.getInstance().enqueueAnalyticsRequest(
					new AnalyticsRequest(AnalyticsRequestType.MEDIAN_RATING_FOR_DRIVER, this, params));
			ThreadMonitor.waitUntilResponse(this);
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to retrieve totalTrips info due to Interrupt Exception");
		}
		
	}

}
