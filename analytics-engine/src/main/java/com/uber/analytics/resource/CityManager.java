package com.uber.analytics.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.uber.data.DataManager;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.AnalyticsRequestType;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Server resource which handles the average fare by city
 * @author pmurugesan
 *
 */
public class CityManager extends ServerResource {
	
	private int cityNo;
	private static final Logger LOGGER = Logger.getLogger(CityManager.class
			.getName());
	
	/**
	 * Initialize the city no variable
	 */
	@Override
	protected void doInit()  {
		this.cityNo = Integer.parseInt(getAttribute(RequestParams.CITY_NO));
	}
	
	/**
	 * Compute average far by city
	 */
	@Get
	public void cityRequest() {
		try {
			// read all parameters
			String startDate = this.getQuery().getFirstValue(RequestParams.START_DATE);
			String endDate = this.getQuery().getFirstValue(RequestParams.END_DATE);
			
			// create additional params
			Map<String, Object> params = new HashMap<String, Object>();
			if(startDate != null && endDate != null) {
				params.put(RequestParams.START_DATE, UberDateUtils.parseDate(startDate));
				params.put(RequestParams.END_DATE, UberDateUtils.parseDate(endDate));
			}
			validateCityNo(this.cityNo);
			params.put(RequestParams.CITY_NO, this.cityNo);
			
			DataManager.getInstance().enqueueAnalyticsRequest(
					new AnalyticsRequest(AnalyticsRequestType.AVG_FARE_BY_CITY, this, params));
			ThreadMonitor.waitUntilResponse(this);
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to retrieve totalTrips info due to Interrupt Exception");
		}
		
	}

	/**
	 * Validate city no
	 * @param cityNo
	 */
	private void validateCityNo(int cityNo) {
		if(cityNo < 1 || cityNo > 162) {
			throw new IllegalArgumentException("City Number out of bounds for city no:" + cityNo);
		}
		
	}

}
