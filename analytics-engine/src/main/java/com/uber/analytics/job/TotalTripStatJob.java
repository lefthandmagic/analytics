package com.uber.analytics.job;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;

import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.uber.data.DataManager;
import com.uber.helpers.ResponseHelper;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Get total trip stats
 * 
 * @author pmurugesan
 * 
 */
public class TotalTripStatJob implements AnalyticsJob {

	/**
	 * Handle the total trip quest
	 */
	public void handle(AnalyticsRequest request) {
		// resp string
		String resp;
		DataManager dm = DataManager.getInstance();

		// get parameters
		Map<String, Object> params = request.getParams();
		if (params.containsKey(RequestParams.START_DATE)
				&& params.containsKey(RequestParams.END_DATE)) {
			// if date range, compute total trips
			resp = computeTotalTripsForRange(
					(Date) params.get(RequestParams.START_DATE),
					(Date) params.get(RequestParams.END_DATE));
		} else if (params.containsKey(RequestParams.LAST_HOUR)) {
			// get set between last hour - current time
			SortedSet<Date> timeRangeSet = dm.getLastHourTrips().subSet(
					UberDateUtils.anHourBack(), new Date());
			resp = createResponseString(timeRangeSet.size());
		} else {
			Long totalTrips = dm.getTotalTrips().get();
			resp = createResponseString(totalTrips);
		}
		// write response
		request.getRespResource().getResponse()
				.setEntity(resp, MediaType.TEXT_PLAIN);
		request.getRespResource().getResponse().setStatus(Status.SUCCESS_OK);
		ThreadMonitor.NotifyRequestComplete(request.getRespResource());
	}

	/**
	 * compute the total trips between date ranges
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private String computeTotalTripsForRange(Date start, Date end) {
		DataManager dm = DataManager.getInstance();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(UberDateUtils.trimTime(start));
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(UberDateUtils.trimTime(end));
		long sum = 0;
		for (Date date = startDate.getTime(); !startDate.after(endDate); startDate
				.add(Calendar.DATE, 1), date = startDate.getTime()) {
			AtomicLong countByDay = dm.getTripsByDate().get(date);
			if (countByDay != null) {
				sum += countByDay.get();
			}
		}
		return ResponseHelper.dateRangeResponseString(start, end)
				+ createResponseString(sum);
	}

	/**
	 * Helper used to create the response string
	 * 
	 * @param cityNo
	 * @param average
	 * @return
	 */
	private String createResponseString(long totalTrips) {
		StringBuilder sb = new StringBuilder("Total Trips: ")
				.append(totalTrips);
		return sb.toString();
	}
}
