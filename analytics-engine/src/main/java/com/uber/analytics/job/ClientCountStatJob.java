package com.uber.analytics.job;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.uber.data.DataManager;
import com.uber.helpers.ResponseHelper;
import com.uber.helpers.UberDateUtils;
import com.uber.request.AnalyticsRequest;
import com.uber.request.RequestParams;
import com.uber.request.ThreadMonitor;

/**
 * Job to count unique clients
 * @author pmurugesan
 *
 */
public class ClientCountStatJob implements AnalyticsJob {

	/**
	 * Handle request for unique clients
	 */
	public void handle(AnalyticsRequest request) {
		String resp;
		DataManager dm = DataManager.getInstance();
		// read params
		Map<String, Object> params = request.getParams();
		// if date range present
		if (params.containsKey(RequestParams.START_DATE)
				&& params.containsKey(RequestParams.END_DATE)) {
			resp = computeTotalClientsForRange(
					(Date) params.get(RequestParams.START_DATE),
					(Date) params.get(RequestParams.END_DATE));
		} else {
			Integer totalUniqueClients = dm.getUniqueClients().size();
			resp = createResponseString(totalUniqueClients);
		}

		// set response
		request.getRespResource().getResponse()
				.setEntity(resp, MediaType.TEXT_PLAIN);
		request.getRespResource().getResponse().setStatus(Status.SUCCESS_OK);
		ThreadMonitor.NotifyRequestComplete(request.getRespResource());

	}

	/**
	 * Compute the total clients for date range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private String computeTotalClientsForRange(Date start, Date end) {
		DataManager dm = DataManager.getInstance();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(UberDateUtils.trimTime(start));
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(UberDateUtils.trimTime(end));
		Set<String> uniqueClients = new HashSet<String>();
		for (Date date = startDate.getTime(); !startDate.after(endDate); startDate
				.add(Calendar.DATE, 1), date = startDate.getTime()) {
			ConcurrentMap<String, Object> clientsByDay = dm.getClientsByDate()
					.get(date);
			if (clientsByDay != null) {
				uniqueClients.addAll(clientsByDay.keySet());
			}
		}
		return ResponseHelper.dateRangeResponseString(start, end)
				+ createResponseString(uniqueClients.size());
	}

	/**
	 * Helper used to create the response string
	 * 
	 * @param cityNo
	 * @param average
	 * @return
	 */
	private String createResponseString(Integer totalUniqueClients) {
		StringBuilder sb = new StringBuilder("Number of Clients: ")
				.append(totalUniqueClients);
		return sb.toString();
	}

}
