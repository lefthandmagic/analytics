package com.uber.analytics.job;

import java.util.Map;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.uber.data.DataManager;
import com.uber.helpers.Median;
import com.uber.request.AnalyticsRequest;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;
/**
 * Get the driver median rating
 * @author pmurugesan
 *
 */
public class DriverStatJob implements AnalyticsJob {

	/**
	 * handler to find median rating for driver
	 */
	public void handle(AnalyticsRequest request) {
		
		Map<String, Object> params = request.getParams();
		String resp = medianRatingForDriver((String) params.get(RequestParams.DRIVER_ID));
		request.getRespResource().getResponse()
				.setEntity(resp, MediaType.TEXT_PLAIN);
		request.getRespResource().getResponse().setStatus(Status.SUCCESS_OK);
		ThreadMonitor.NotifyRequestComplete(request.getRespResource());
	}

	/**
	 * Find the median rating for driver and return response string
	 * @param driverId
	 * @return
	 */
	private String medianRatingForDriver(String driverId) {
		DataManager dm = DataManager.getInstance();
		Median median = dm.getMedianByDriver().get(driverId);
		if(median != null) {
			StringBuilder sb = new StringBuilder(
					"Median Rating for Driver: ").append(driverId)
					.append(" is: ").append(median.getMedian());
			return sb.toString();
		}
		return "Driver Does not have any trips yet";
	}

}
