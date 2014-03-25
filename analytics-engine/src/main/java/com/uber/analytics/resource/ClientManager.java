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
 * Server Resource which handles Client info stats
 * @author pmurugesan
 *
 */
public class ClientManager extends ServerResource {
	
	private String type;
	private String clientId;
	
	/**
	 * Initialize the type of request and client id
	 */
	@Override
	protected void doInit()  {
		this.type = getAttribute(RequestParams.REQUEST_TYPE);
		this.clientId = getAttribute(RequestParams.CLIENT_ID);
	}

	// logger
	private static final Logger LOGGER = Logger.getLogger(ClientManager.class
			.getName());
	/**
	 * Handles a request of total no of clients who've taken trips
	 * and also total miles per client
	 */
	@Get
	public void clientRequest() {
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
			// get request type
			AnalyticsRequestType requestType = null;
			if(this.type != null && this.type.equalsIgnoreCase(RequestParams.MILES)) {
				requestType = AnalyticsRequestType.TOTAL_MILES_BY_CLIENT;
				params.put(RequestParams.CLIENT_ID, this.clientId);
			} else if(this.type != null && this.type.equalsIgnoreCase(RequestParams.COUNT)) {
				requestType = AnalyticsRequestType.TOTAL_CLIENTS;
			} else {
				throw new IllegalArgumentException("Request type is invalid: " + this.type);
			}
			DataManager.getInstance().enqueueAnalyticsRequest(
					new AnalyticsRequest(requestType, this, params));
			ThreadMonitor.waitUntilResponse(this);
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to retrieve totalTrips info due to Interrupt Exception");
		}
	}

}
