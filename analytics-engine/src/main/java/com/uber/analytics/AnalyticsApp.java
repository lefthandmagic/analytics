package com.uber.analytics;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.uber.analytics.resource.CityManager;
import com.uber.analytics.resource.ClientManager;
import com.uber.analytics.resource.DriverManager;
import com.uber.analytics.resource.TripManager;

/**
 * Definition of the analytics app. The router for the rest application is
 * defined here
 * 
 * @author pmurugesan
 * 
 */
public class AnalyticsApp extends Application {
	// logger
	private static final Logger LOGGER = Logger.getLogger(AnalyticsApp.class
			.getName());

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());

		// end point to query all trip count information
		router.attach("/trips", TripManager.class);
		// end point to query client information (i.e, number of unique clients)
		router.attach("/clients/{type}", ClientManager.class);
		// end point to query client specific information, i.e miles traveled by
		router.attach("/clients/{type}/{client_id}", ClientManager.class);
		// end point to query driver median rating
		router.attach("/drivers/{driver_id}/rating/median", DriverManager.class);
		// end point to query city fare average
		router.attach("/city/{city_no}/fare/avg", CityManager.class);
		LOGGER.info("Registered various Routes");
		return router;
	}

}
