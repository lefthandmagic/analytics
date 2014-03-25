package com.uber.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilities to do date manipulations
 * 
 * @author pmurugesan
 * 
 */
public class UberDateUtils {

	// the standard date format for the application
	private static final SimpleDateFormat dateParser = new SimpleDateFormat(
			"EEE MMM d HH:mm:ss Z yyyy");

	/**
	 * Convert from string to date
	 * 
	 * @param startTime
	 * @return
	 */
	public static Date parseDate(String startTime) {
		try {
			return dateParser.parse(startTime);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"The start time cannot be parsed" + e.getMessage());
		}
	}

	/**
	 * Trim the time value from the Date
	 * 
	 * @param date
	 * @return
	 */
	public static Date trimTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Compute the time one hour ago from the current time
	 * 
	 * @return
	 */
	public static Date anHourBack() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -1);
		return cal.getTime();
	}

}
