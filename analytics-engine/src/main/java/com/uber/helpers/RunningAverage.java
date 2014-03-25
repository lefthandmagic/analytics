package com.uber.helpers;

/**
 * Helper class to store the running average
 * 
 * @author pmurugesan
 * 
 */
public class RunningAverage {

	// average
	private final double average;
	// total value
	private final double total;

	// constructor
	public RunningAverage(double average, double total) {
		this.average = average;
		this.total = total;
	}

	// accessors
	public double average() {
		return average;
	}

	public double total() {
		return total;
	}

}
