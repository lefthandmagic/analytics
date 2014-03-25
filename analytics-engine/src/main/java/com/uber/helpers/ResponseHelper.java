package com.uber.helpers;

import java.util.Date;

/**
 * Helper for generating response strings
 * @author pmurugesan
 *
 */
public class ResponseHelper {
	
	// non-instantiable constructor
	private ResponseHelper() {}
	
	/**
	 * Response string for date range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String dateRangeResponseString(Date start, Date end) {
		return new StringBuilder("For Date Range: ").append(start)
				.append(" and ").append(end).append(" ").toString();
	}

}
