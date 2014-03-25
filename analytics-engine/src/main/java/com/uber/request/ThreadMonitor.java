package com.uber.request;

import org.restlet.resource.ServerResource;

/**
 * Helper method to block/release original request thread until/after the async
 * thread processes the request
 * 
 * @author pmurugesan
 * 
 */
public final class ThreadMonitor {

	// cannot instantiate
	private ThreadMonitor() {
	}

	/**
	 * Block on server resource and wait until response
	 * @param resource
	 * @throws InterruptedException
	 */
	public static void waitUntilResponse(ServerResource resource)
			throws InterruptedException {
		synchronized (resource) {
			while (resource.getResponse().getEntity() == null) {
				resource.wait();
			}
		}
	}

	/**
	 * Notify server resource of response
	 * @param serverResource
	 */
	public static void NotifyRequestComplete(ServerResource serverResource) {
		synchronized (serverResource) {
			serverResource.notify();
		}
	}

}
