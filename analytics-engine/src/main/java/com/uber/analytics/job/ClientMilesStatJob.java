package com.uber.analytics.job;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.uber.data.DataManager;
import com.uber.helpers.ResponseHelper;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Job to get total miles by client
 * 
 * @author pmurugesan
 * 
 */
public class ClientMilesStatJob implements AnalyticsJob {

	/**
	 * handle total miles for a specific client
	 */
	public void handle(AnalyticsRequest request) {
		String resp;
		// read params
		Map<String, Object> params = request.getParams();
		// if date range present
		if (params.containsKey(RequestParams.START_DATE)
				&& params.containsKey(RequestParams.END_DATE)) {
			resp = computeTotalMilesForRange(
					(String) params.get(RequestParams.CLIENT_ID),
					(Date) params.get(RequestParams.START_DATE),
					(Date) params.get(RequestParams.END_DATE));
		} else {
			// compute miles for all dates
			resp = computeTotalMilesForRange((String) params
					.get(RequestParams.CLIENT_ID));
		}

		// set response
		request.getRespResource().getResponse()
				.setEntity(resp, MediaType.TEXT_PLAIN);
		request.getRespResource().getResponse().setStatus(Status.SUCCESS_OK);
		ThreadMonitor.NotifyRequestComplete(request.getRespResource());

	}

	/**
	 * Compute total miles for all dates for client
	 * 
	 * @param clientId
	 * @return
	 */
	private String computeTotalMilesForRange(String clientId) {
		DataManager dm = DataManager.getInstance();
		double totalMiles = 0;
		for (Entry<Date, ConcurrentMap<String, AtomicReference<Double>>> milesByDay : dm
				.getMilesPerClientByDate().entrySet()) {
			ConcurrentMap<String, AtomicReference<Double>> clientMiles = milesByDay
					.getValue();
			totalMiles = runningSum(clientId, totalMiles, clientMiles);
		}
		return createResponseString(clientId, totalMiles);
	}

	/**
	 * Helper to keep running sum of miles per client id
	 * 
	 * @param clientId
	 * @param totalMiles
	 * @param clientMiles
	 * @return
	 */
	private double runningSum(String clientId, double totalMiles,
			ConcurrentMap<String, AtomicReference<Double>> clientMiles) {
		if (clientMiles != null) {
			totalMiles = clientMiles.containsKey(clientId) ? totalMiles
					+ clientMiles.get(clientId).get() : totalMiles;
		}
		return totalMiles;
	}

	/**
	 * Compute total miles for client across date range
	 * 
	 * @param clientId
	 * @param start
	 * @param end
	 * @return
	 */
	private String computeTotalMilesForRange(String clientId, Date start,
			Date end) {
		double totalMiles = 0;
		DataManager dm = DataManager.getInstance();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(UberDateUtils.trimTime(start));
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(UberDateUtils.trimTime(end));
		for (Date date = startDate.getTime(); !startDate.after(endDate); startDate
				.add(Calendar.DATE, 1), date = startDate.getTime()) {
			ConcurrentMap<String, AtomicReference<Double>> clientMiles = dm
					.getMilesPerClientByDate().get(date);
			totalMiles = runningSum(clientId, totalMiles, clientMiles);
		}

		return ResponseHelper.dateRangeResponseString(start, end)
				+ createResponseString(clientId, totalMiles);
	}

	/**
	 * Helper to create response string
	 * @param clientId
	 * @param totalMiles
	 * @return
	 */
	private String createResponseString(String clientId, double totalMiles) {
		StringBuilder sb = new StringBuilder(
				"Total number of miles for client: ").append(clientId)
				.append(" is: ").append(totalMiles);
		return sb.toString();
	}

}
