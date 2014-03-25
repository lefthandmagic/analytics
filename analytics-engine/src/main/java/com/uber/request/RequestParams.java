package com.uber.request;

/**
 * Helper non-instantiable class to store static Request Params
 * @author pmurugesan
 *
 */
public final class RequestParams {
	
	// don't allow instantiation
	private RequestParams() {};
	
	public static final String TRIP_PARAM = "trip";
	public static final String START_DATE = "start";
	public static final String END_DATE = "end";
	public static final String LAST_HOUR = "last";
	public static final String CLIENT_ID = "client_id";
	public static final String DRIVER_ID = "driver_id";
	public static final String CITY_NO = "city_no";
	public static final String START_TIME = "start_time";
	public static final String LATITUTDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String FARE = "fare";
	public static final String DISTANCE = "distance";
	public static final String RATING = "rating";
	public static final String REQUEST_TYPE = "type";
	public static final String MILES = "miles";
	public static final String COUNT = "count";
	
}
