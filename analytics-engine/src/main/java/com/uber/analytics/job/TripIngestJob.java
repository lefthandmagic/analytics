package com.uber.analytics.job;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.restlet.data.Status;

import com.uber.analytics.resource.TripManager;
import com.uber.data.DataManager;
import com.uber.data.Trip;
import com.uber.helpers.LatitudeLongitude;
import com.uber.helpers.Median;
import com.uber.helpers.RunningAverage;
import com.uber.request.AnalyticsRequest;
import com.uber.request.AnalyticsRequestType;
import com.uber.request.RequestParams;

/**
 * handle new trips ingestion
 * 
 * @author pmurugesan
 * 
 */
public class TripIngestJob implements AnalyticsJob {

	// logger
	private static final Logger LOGGER = Logger.getLogger(TripManager.class
			.getName());

	/**
	 * new trip handler
	 */
	public void handle(AnalyticsRequest request) {
		Trip t = (Trip) request.getParams().get(RequestParams.TRIP_PARAM);

		// add to trip count data structures
		addToTripCount(t);
		// store client info
		addToClientInfo(t);
		// recalculate median rating for driver
		recalculateMedianRating(t);
		// recalculate avg far for city
		recalculateAvgFareByCity(t);
		// response - success
		request.getRespResource()
				.getResponse()
				.setStatus(Status.SUCCESS_OK,
						"Ingestion Complete for trip: " + t);
	}

	/**
	 * recalculate the average far by city
	 * 
	 * @param trip
	 */
	private void recalculateAvgFareByCity(Trip trip) {
		LatitudeLongitude coord = new LatitudeLongitude(trip.getLatitutde(),
				trip.getLongitude());
		DataManager dmInstance = DataManager.getInstance();
		Date trimmedDate = trip.getTimeTrimmedCreatedDate();

		// get the city fare for date
		ConcurrentMap<Integer, AtomicReference<RunningAverage>> cityFareByDate = dmInstance
				.getAvgCityFareByDate()
				.putIfAbsent(
						trimmedDate,
						new ConcurrentHashMap<Integer, AtomicReference<RunningAverage>>());
		if (cityFareByDate == null) {
			cityFareByDate = dmInstance.getAvgCityFareByDate().get(trimmedDate);
		}

		AtomicReference<RunningAverage> average = cityFareByDate.putIfAbsent(
				coord.getCity(), new AtomicReference<RunningAverage>(
						new RunningAverage(0, 0)));
		if (average == null) {
			average = cityFareByDate.get(coord.getCity());
		}
		setNewAverage(average, trip.getFare());
	}

	/**
	 * Helper to create a new fare, using atomic reference as it's more
	 * efficient than synchronized locks
	 * 
	 * @param average
	 * @param fare
	 */
	private void setNewAverage(AtomicReference<RunningAverage> average,
			double fare) {
		RunningAverage current, updated;
		do {
			current = average.get();
			updated = new RunningAverage(
					((current.total() * current.average()) + fare)
							/ (current.total() + 1), current.total() + 1);
		} while (!average.compareAndSet(current, updated));
	}

	/**
	 * Helper to recalculate median rating
	 * @param trip
	 */
	private void recalculateMedianRating(Trip trip) {
		DataManager dmInstance = DataManager.getInstance();
		Median median = dmInstance.getMedianByDriver().putIfAbsent(
				trip.getDriverId(), new Median());
		if (median == null) {
			median = dmInstance.getMedianByDriver().get(trip.getDriverId());
		}
		// add trip rating to median data structure
		median.addValue(trip.getRating());
	}

	/**
	 * Helper to add client info into data structures
	 * @param trip
	 */
	private void addToClientInfo(Trip trip) {
		DataManager dmInstance = DataManager.getInstance();
		
		// store unique client set
		dmInstance.getUniqueClients().putIfAbsent(trip.getClientId(),
				new Object());

		// aggregate list of clients
		Date trimmedDate = trip.getTimeTrimmedCreatedDate();
		ConcurrentMap<String, Object> uniqueClientList = dmInstance
				.getClientsByDate().putIfAbsent(trimmedDate,
						new ConcurrentHashMap<String, Object>());
		if (uniqueClientList == null) {
			uniqueClientList = dmInstance.getClientsByDate().get(trimmedDate);
		}
		uniqueClientList.putIfAbsent(trip.getClientId(), new Object());

		// aggregate miles per client/day
		ConcurrentMap<String, AtomicReference<Double>> milesByClient = dmInstance
				.getMilesPerClientByDate()
				.putIfAbsent(
						trimmedDate,
						new ConcurrentHashMap<String, AtomicReference<Double>>());
		if (milesByClient == null) {
			milesByClient = dmInstance.getMilesPerClientByDate().get(
					trimmedDate);
		}
		AtomicReference<Double> distance = milesByClient.putIfAbsent(
				trip.getClientId(),
				new AtomicReference<Double>(trip.getDistance()));
		if (distance != null) {
			setNewSum(distance, trip.getDistance());
		}
	}

	/**
	 * Add trip to trip count data structures
	 * @param trip
	 */
	private void addToTripCount(Trip trip) {
		DataManager dmInstance = DataManager.getInstance();
		// up total trips
		dmInstance.getTotalTrips().incrementAndGet();
		Date trimmedDate = trip.getTimeTrimmedCreatedDate();
		
		// up total trips by date
		AtomicLong val = dmInstance.getTripsByDate().putIfAbsent(trimmedDate,
				new AtomicLong(1));
		// if already present
		if (val != null) {
			val.incrementAndGet();
		}
		if (getDateDiff(trip.getCreatedDate(), new Date(), TimeUnit.HOURS) < 1) {
			// add to last hour trips
			dmInstance.getLastHourTrips().add(trip.getCreatedDate());
		}
		try {
			// take the chance to queue a cleanup request
			dmInstance.enqueueAnalyticsRequest(new AnalyticsRequest(
					AnalyticsRequestType.CLEANUP_LAST_HOUR_LIST, null));
		} catch (InterruptedException e) {
			LOGGER.severe("Unable to enqueue cleanup last hour list job due to Interrupt Exception");
		}
	}

	/**
	 * Helper to get difference in dates
	 * @param date1
	 * @param date2
	 * @param timeUnit
	 * @return
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	/**
	 * Helper to set new sum of total miles
	 * @param totalMiles
	 * @param distance
	 */
	private void setNewSum(AtomicReference<Double> totalMiles, double distance) {
		Double current, updated;
		do {
			current = totalMiles.get();
			updated = current + distance;
		} while (!totalMiles.compareAndSet(current, updated));
	}

}
