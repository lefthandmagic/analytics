package com.uber.analytics.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.uber.data.DataManager;
import com.uber.data.Trip;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.AnalyticsRequestType;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Server resource which handles trip ingestion and querying general stats about
 * trips count
 * 
 * @author pmurugesan
 * 
 */
public class TripManager extends ServerResource {

	// logger
	private static final Logger LOGGER = Logger.getLogger(TripManager.class
			.getName());

	/**
	 * Query the total number of trips with date range as optional params. Query
	 * trips in the last hour
	 * 
	 */
	@Get
	public void totalTrips() {
		try {
			// read query params
			String startDate = this.getQuery().getFirstValue(
					RequestParams.START_DATE);
			String endDate = this.getQuery().getFirstValue(
					RequestParams.END_DATE);
			boolean lHour = this.getQuery().getFirst(RequestParams.LAST_HOUR) != null;
			
			// populate additional params list
			Map<String, Object> params = new HashMap<String, Object>();
			if (startDate != null && endDate != null) {
				params.put(RequestParams.START_DATE,
						UberDateUtils.parseDate(startDate));
				params.put(RequestParams.END_DATE,
						UberDateUtils.parseDate(endDate));
			} else if (lHour) {
				params.put(RequestParams.LAST_HOUR, true);
			}
			
			// enqueue analytics request
			DataManager.getInstance().enqueueAnalyticsRequest(
					new AnalyticsRequest(AnalyticsRequestType.TOTAL_TRIPS,
							this, params));
			ThreadMonitor.waitUntilResponse(this);
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to retrieve trips info due to Interrupt Exception");
		}
	}

	/**
	 * ingest api to add new trips
	 * 
	 * @throws IOException
	 */
	@Put
	public void storeTrip(Representation entity) throws IOException {
		Form form = new Form(entity);
		Trip t = createNewTrip(form.getFirstValue(RequestParams.CLIENT_ID)
				.trim(), form.getFirstValue(RequestParams.DRIVER_ID).trim(),
				form.getFirstValue(RequestParams.START_TIME).trim(), form
						.getFirstValue(RequestParams.FARE).trim(), form
						.getFirstValue(RequestParams.DISTANCE).trim(), form
						.getFirstValue(RequestParams.RATING).trim(), form
						.getFirstValue(RequestParams.LATITUTDE).trim(), form
						.getFirstValue(RequestParams.LONGITUDE).trim());
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(RequestParams.TRIP_PARAM, t);
			// enqueue ingest request
			DataManager.getInstance().enqueueAnalyticsRequest(
					new AnalyticsRequest(AnalyticsRequestType.INGEST, this,
							params));
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to insert Trip: " + t.toString()
					+ " due to Interrupt Exception");
		}
	}

	/**
	 * Helper to create a new trip object
	 * 
	 * @param clientId
	 * @param driverId
	 * @param startTime
	 * @param fare
	 * @param distance
	 * @param rating
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Trip createNewTrip(String clientId, String driverId,
			String startTime, String fare, String distance, String rating,
			String latitude, String longitude) {
		return new Trip(clientId, driverId, startTime, fare, distance, rating,
				latitude, longitude);
	}

}
