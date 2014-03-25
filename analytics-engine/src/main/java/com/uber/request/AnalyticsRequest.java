package com.uber.request;

import java.util.Map;

import org.restlet.resource.ServerResource;
/**
 * Analytics Request Query which is passed on to async threads
 * @author pmurugesan
 *
 */
public class AnalyticsRequest {
	
	// additional params for the request
	private final Map<String, Object> params;
	// the server resource handling the request
	private final ServerResource respResource;
	// the type of analytics request, this is used to route to the right job
	private final AnalyticsRequestType type;

	// constructor
	public AnalyticsRequest(AnalyticsRequestType type, ServerResource resp) {
		this(type, resp, null);
	}
	
	// constructor
	public AnalyticsRequest(AnalyticsRequestType type, ServerResource respResource, Map<String, Object> params) {
		this.params = params;
		this.respResource = respResource;
		this.type = type;
	}
	
	/**
	 * Accessors
	 */
	
	public Map<String, Object> getParams() {
		return this.params;
	}

	public ServerResource getRespResource() {
		return respResource;
	}

	public AnalyticsRequestType getType() {
		return type;
	}

}
