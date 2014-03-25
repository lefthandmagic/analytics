package com.uber.data;

import java.util.Date;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.uber.analytics.job.AnalyticsJob;
import com.uber.analytics.job.AnalyticsJobFactory;
import com.uber.helpers.Median;
import com.uber.helpers.RunningAverage;
import com.uber.request.AnalyticsRequest;

/**
 * Singleton object which stores all data in various data structures
 * 
 * @author pmurugesan
 * 
 */
public class DataManager {
	
	// instance
	private static DataManager instance = null;
	private final AtomicLong threadCount;

	// atomic value to keep account of total trips across all dates
	private final AtomicLong totalTrips;

	// concurrent hash map to keep track of total trips by individual days
	private final ConcurrentMap<Date, AtomicLong> tripsByDate;

	// concurrent hash map to keep track of unique clients encountered by day.
	// using map because concurrent hash set doesn't exist, either way hash sets
	// are impls of hash maps, so :)
	private final ConcurrentMap<String, Object> uniqueClientSet;
	
	// concurrent hash map of driver id to median of rating
	private final ConcurrentMap<String, Median> mediaRatingByDriver;
	
	// concurrent hash map of client set by individual days
	private final ConcurrentMap<Date, ConcurrentMap<String, Object>> uniqueClientSetByDate;
	
	// concurrent hash map of miles per client by individual days
	private final ConcurrentMap<Date, ConcurrentMap<String, AtomicReference<Double>>> milesPerClientByDate;
	
	// concurrent hash map of average city fare by individual days
	private final ConcurrentMap<Date, ConcurrentMap<Integer, AtomicReference<RunningAverage>>> avgCityFareByDate;
	
	// sorted set of dates of trips taken in the last one hour
	private final SortedSet<Date> lastHourTrips;
	
	// Blocking Queue which the thread pool works off, as various requests come in
	private final BlockingQueue<AnalyticsRequest> queue = new LinkedBlockingQueue<AnalyticsRequest>();

	// the thread pool that manages async processing
	private final ExecutorService threadPool;
	/**
	 * Singleton instance
	 * @return
	 */
	public static synchronized DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	// constructor
	private DataManager() {
		this.threadCount = new AtomicLong(0);
		this.totalTrips = new AtomicLong(0);
		this.tripsByDate = new ConcurrentHashMap<Date, AtomicLong>();
		this.uniqueClientSet = new ConcurrentHashMap<String, Object>();
		this.mediaRatingByDriver = new ConcurrentHashMap<String, Median>();
		this.uniqueClientSetByDate = new ConcurrentHashMap<Date, ConcurrentMap<String, Object>>();
		this.milesPerClientByDate = new ConcurrentHashMap<Date, ConcurrentMap<String, AtomicReference<Double>>>();
		this.avgCityFareByDate = new ConcurrentHashMap<Date, ConcurrentMap<Integer, AtomicReference<RunningAverage>>>();
		this.lastHourTrips = new ConcurrentSkipListSet<Date>();
		this.threadPool = Executors.newCachedThreadPool();
		this.threadPool.execute(new WorkerThread("Thread"
				+ this.threadCount.incrementAndGet()));
	}

	/**
	 * Method where new analtyics requests are enqueued into the blocking queue
	 * @param r
	 * @throws InterruptedException
	 */
	public void enqueueAnalyticsRequest(AnalyticsRequest r)
			throws InterruptedException {
		queue.put(r);
	}


	/**
	 * Worker thread implementation
	 * @author pmurugesan
	 *
	 */
	public class WorkerThread implements Runnable {

		private final String name;

		public WorkerThread(String name) {
			this.name = name;
		}

		public void run() {
			while (true) {
				try {
					// wait in blocking queue for new request
					AnalyticsRequest request = queue.take();
					AnalyticsJob job = AnalyticsJobFactory
							.createAnalyticsJob(request.getType());
					// call handle method on job based upon type
					job.handle(request);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		public String getName() {
			return name;
		}

	}
	
	/**
	 * Accessors
	 */
	
	public AtomicLong getTotalTrips() {
		return totalTrips;
	}

	public ConcurrentMap<Date, AtomicLong> getTripsByDate() {
		return tripsByDate;
	}

	public ConcurrentMap<String, Object> getUniqueClients() {
		return uniqueClientSet;
	}

	public ConcurrentMap<String, Median> getMedianByDriver() {
		return mediaRatingByDriver;
	}

	public ConcurrentMap<Date, ConcurrentMap<String, Object>> getClientsByDate() {
		return uniqueClientSetByDate;
	}

	public ConcurrentMap<Date, ConcurrentMap<String, AtomicReference<Double>>> getMilesPerClientByDate() {
		return milesPerClientByDate;
	}

	public ConcurrentMap<Date, ConcurrentMap<Integer, AtomicReference<RunningAverage>>> getAvgCityFareByDate() {
		return avgCityFareByDate;
	}

	public SortedSet<Date> getLastHourTrips() {
		return lastHourTrips;
	}

}
