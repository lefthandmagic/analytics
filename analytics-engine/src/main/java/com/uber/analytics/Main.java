package com.uber.analytics;

import java.util.logging.Logger;

import org.restlet.Component;
import org.restlet.data.Protocol;


/**
 * The Main Class is the prime class to start the rest service
 * 
 * @author pmurugesan
 * 
 */
public class Main {

	// port
	private static final int HTTP_PORT = 8182;
	// logger
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a new rest let Component.
		Component component = new Component();

		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, HTTP_PORT);

		// Attach the analytics application
		component.getDefaultHost().attach(new AnalyticsApp());
		// Start the component.
		try {
			component.start();
		} catch (Exception e) {
			LOGGER.severe("Unable to start restful service");
		}
	}

}
