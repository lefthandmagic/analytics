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
import com.uber.helpers.RunningAverage;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Analytics Job which computes the stats about the city.
 * 
 * @author pmurugesan
 * 
 */
public class CityStatJob implements AnalyticsJob {

	/**
	 * Handle the city average fare request
	 */
	public void handle(AnalyticsRequest request) {
		// response string
		String resp;
		Map<String, Object> params = request.getParams();

		if (params.containsKey(RequestParams.START_DATE)
				&& params.containsKey(RequestParams.END_DATE)) {
			// if request has a date range compute average fare for the city
			// for that particular date range
			resp = avgFareForSpecificCity(
					(Integer) params.get(RequestParams.CITY_NO),
					(Date) params.get(RequestParams.START_DATE),
					(Date) params.get(RequestParams.END_DATE));
		} else {
			// compute the avg fare for the city across all data.
			resp = avgFareForSpecificCity((Integer) params
					.get(RequestParams.CITY_NO));
		}
		// write the response back
		request.getRespResource().getResponse()
				.setEntity(resp, MediaType.TEXT_PLAIN);
		request.getRespResource().getResponse().setStatus(Status.SUCCESS_OK);
		// notify waiting request thread that the request is now complete
		ThreadMonitor.NotifyRequestComplete(request.getRespResource());
	}

	/**
	 * Compute the average fare for the city across all data
	 * 
	 * @param cityNo
	 * @return
	 */
	private String avgFareForSpecificCity(Integer cityNo) {
		// initialize totalAverage & count of entries
		AvgFareState avgFareState = new AvgFareState(0, 0);

		DataManager dm = DataManager.getInstance();

		// loop around all date entries of avg city by fare list
		for (Entry<Date, ConcurrentMap<Integer, AtomicReference<RunningAverage>>> avgCityFareByDay : dm
				.getAvgCityFareByDate().entrySet()) {
			ConcurrentMap<Integer, AtomicReference<RunningAverage>> avgFareByCity = avgCityFareByDay
					.getValue();

			addToRunningAvg(cityNo, avgFareState, avgFareByCity);
		}
		// compute avg across multiple dates
		double average = getAverage(avgFareState.getTotal(),
				avgFareState.getCount());

		return createResponseString(cityNo, average);
	}

	/**
	 * Reusable method to add to running average
	 * 
	 * @param cityNo
	 * @param avgFareState
	 * @param avgFareByCity
	 */
	private void addToRunningAvg(
			Integer cityNo,
			AvgFareState avgFareState,
			ConcurrentMap<Integer, AtomicReference<RunningAverage>> avgFareByCity) {
		if (avgFareByCity != null) {
			// if the date has the city no data, add to the
			// totalAvg/avgCount
			if (avgFareByCity.containsKey(cityNo)) {
				RunningAverage avg = avgFareByCity.get(cityNo).get();
				avgFareState.setTotal(avg.average() + avgFareState.getTotal());
				avgFareState.setCount(avgFareState.getCount() + 1);
			}
		}
	}

	/**
	 * Compute the average fare for the city between dates
	 * 
	 * @param cityNo
	 * @param start
	 * @param end
	 * @return
	 */
	private String avgFareForSpecificCity(Integer cityNo, Date start, Date end) {
		// initialize totalAverage & count of entries
		AvgFareState avgFareState = new AvgFareState(0, 0);

		DataManager dm = DataManager.getInstance();

		// Initialize date variables to iterate over date range
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(UberDateUtils.trimTime(start));
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(UberDateUtils.trimTime(end));

		// iterate over date range
		for (Date date = startDate.getTime(); !startDate.after(endDate); startDate
				.add(Calendar.DATE, 1), date = startDate.getTime()) {
			// get average fare by city
			ConcurrentMap<Integer, AtomicReference<RunningAverage>> avgFareByCity = dm
					.getAvgCityFareByDate().get(date);
			addToRunningAvg(cityNo, avgFareState, avgFareByCity);
		}
		// compute average
		double average = getAverage(avgFareState.getTotal(),
				avgFareState.getCount());

		return ResponseHelper.dateRangeResponseString(start, end)
				+ createResponseString(cityNo, average);
	}

	/**
	 * Helper to compute average
	 * 
	 * @param totalAvg
	 * @param avgCount
	 * @return
	 */
	private double getAverage(double total, int count) {
		return (count > 0) ? total / count : 0;
	}

	/**
	 * Helper used to create the response string
	 * 
	 * @param cityNo
	 * @param average
	 * @return
	 */
	private String createResponseString(Integer cityNo, double average) {
		StringBuilder sb = new StringBuilder("Average Fare for City: ")
				.append(cityNo).append(" is: ").append(average);
		return sb.toString();
	}

	// helper class to keep avg fare state
	private static class AvgFareState {
		private int count;
		private double total;

		AvgFareState(double total, int count) {
			this.setCount(count);
			this.setTotal(total);
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public double getTotal() {
			return total;
		}

		public void setTotal(double total) {
			this.total = total;
		}
	}

}
