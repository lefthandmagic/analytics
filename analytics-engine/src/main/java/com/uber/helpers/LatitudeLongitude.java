package com.uber.helpers;

/**
 * This data structure is used to hold the coordinates of the trip
 * @author pmurugesan
 *
 */
public class LatitudeLongitude {
	
	// latitude - between (-90, +90)
	private final float latitude;
	// longitude - between (-180, +180)
	private final float longitutde;
	
	// constructor
	public LatitudeLongitude(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitutde = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitutde() {
		return longitutde;
	}
	
	// assuming the world is split into 18*36 squares and each square is a large city
	// cities ranges from 1 - 648
	public int getCity() {
		// value of 0 - 17;
		int row = (int)Math.floor((latitude + 90)/10);
		// value of 0 - 35
		int column = (int)Math.floor((longitutde + 180)/10);
		return (row * 18 + column) + 1;
	}

}
