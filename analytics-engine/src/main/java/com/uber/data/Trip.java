package com.uber.data;


import java.util.Date;

import com.uber.helpers.UberDateUtils;
import com.uber.request.RequestParams;

/**
 * Object to represent each trip datapoint
 * @author pmurugesan
 *
 */
public class Trip {

	private final String clientId;
	private final String driverId;
	private final Date startTime;
	private final float latitutde;
	private final float longitude;
	private final float fare;
	private final float distance;
	private final int rating;
	private final Date trimmedDate;

	
	// constructor
	public Trip(String clientId, String driverId, String startTime,
			String fare, String distance, String rating, String latitude,
			String longitude) {
		validateId(clientId);
		this.clientId = clientId;
		validateId(driverId);
		this.driverId = driverId;
		float tempFare = Float.parseFloat(fare);
		validateNonNegative(tempFare, RequestParams.FARE);
		this.fare = tempFare;
		float tempDistance = Float.parseFloat(distance);
		validateNonNegative(tempDistance, RequestParams.DISTANCE);
		this.distance = tempDistance;
		float tempLatitude = Float.parseFloat(latitude);
		validateLatitude(tempLatitude);
		this.latitutde = tempLatitude;
		float tempLongitude = Float.parseFloat(longitude);
		validateLongitude(tempLongitude);
		this.longitude = tempLongitude;
		int tempRating = Integer.parseInt(rating);
		validateRating(tempRating);
		this.rating = tempRating;
		this.startTime = UberDateUtils.parseDate(startTime);
		this.trimmedDate = UberDateUtils.trimTime(this.startTime);
	}


	/**
	 * Validate that the rating is between 1 - 5
	 * @param tempRating
	 */
	private void validateRating(int tempRating) {
		if (tempRating < 1 || tempRating > 5) {
			throw new IllegalArgumentException(
					"Illegal rating, outside range (1-5) :" + tempRating);
		}

	}

	/**
	 * validate that the longitude is between (-180, + 180)
	 * @param tempLongitude
	 */
	private void validateLongitude(float tempLongitude) {
		if (tempLongitude < -180 || tempLongitude > 180) {
			throw new IllegalArgumentException(
					"Illegal Longitude, outside range (-180 - +180): "
							+ tempLongitude);
		}
	}

	/**
	 * validate that the latitude is between (-90, + 90)
	 * @param tempLatitude
	 */
	private void validateLatitude(float tempLatitude) {
		if (tempLatitude < -90 || tempLatitude > 90) {
			throw new IllegalArgumentException(
					"Illegal Latitude, outside range (-90 - +90): "
							+ tempLatitude);
		}
	}

	/**
	 * Validate that the given value is not negative
	 * @param value
	 * @param argument
	 */
	private void validateNonNegative(float value, String argument) {
		if (value < 0) {
			throw new IllegalArgumentException(
					"Illegal Argument, cannot be negative." + argument + " : "
							+ value);
		}
	}

	/**
	 * Validate that the given string is an id
	 * @param id
	 */
	private void validateId(String id) {
		if (id.length() != 15) {
			throw new IllegalArgumentException("Illegal Id argument. Id: " + id);
		}
	}

	/**
	 * accessors
	 */
	
	public Date getCreatedDate() {
		return startTime;
	}
	
	public Date getTimeTrimmedCreatedDate() {
		return this.trimmedDate;
	}

	public String getClientId() {
		return clientId;
	}

	public double getDistance() {
		return distance;
	}

	public int getRating() {
		return rating;
	}

	public double getFare() {
		return fare;
	}

	public String getDriverId() {
		return driverId;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitutde() {
		return latitutde;
	}

}
