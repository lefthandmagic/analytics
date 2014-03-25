package com.uber.helpers;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Data structure to keep track of median using two priority queues heaps
 * 
 * The two heaps are balanced in size on insert of new elements, so that the
 * median is computed in O(1) time by reading the head nodes of the min and max
 * heap
 * 
 * @author pmurugesan
 * 
 */
public class Median {
	Queue<Integer> minHeadUpperData;
	Queue<Integer> maxHeapLowerData;

	public Median() {
		// max-heap
		maxHeapLowerData = new PriorityQueue<Integer>(20,
				Collections.reverseOrder());
		// min-heap
		minHeadUpperData = new PriorityQueue<Integer>();
	}

	/**
	 * Add new value to median
	 * @param num
	 * @return
	 */
	public synchronized double addValue(int num) {
		// adding the number to proper heap
		if (minHeadUpperData.peek() != null && num >= minHeadUpperData.peek()) {
			minHeadUpperData.add(num);
		} else {
			maxHeapLowerData.add(num);
		}
		// balancing the heaps
		if (minHeadUpperData.size() - maxHeapLowerData.size() == 2) {
			maxHeapLowerData.add(minHeadUpperData.poll());
		} else if (maxHeapLowerData.size() - minHeadUpperData.size() == 2) {
			minHeadUpperData.add(maxHeapLowerData.poll());
		}
		return getMedian();
	}

	/**
	 * Access the median
	 * @return
	 */
	public synchronized double getMedian() {
		if (minHeadUpperData.size() == 0 && maxHeapLowerData.size() == 0) {
			return 0;
		} else if (minHeadUpperData.size() == maxHeapLowerData.size()) {
			return (minHeadUpperData.peek() + maxHeapLowerData.peek()) / 2.0;
		} else if (minHeadUpperData.size() > maxHeapLowerData.size()) {
			return minHeadUpperData.peek();
		} else {
			return maxHeapLowerData.peek();
		}
	}

}