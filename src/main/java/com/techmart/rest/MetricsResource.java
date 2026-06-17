package com.techmart.rest;

import com.techmart.monitoring.MetricsAggregatorBean;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/metrics")
public class MetricsResource {
    @Inject
    private MetricsAggregatorBean metricsAggregatorBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getMetrics(){
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        Map<String, Map<String, Object>> metricsData = new HashMap<>();

        // Format the metrics into a clean JSON structure for the frontend
        for (Map.Entry<String, MetricsAggregatorBean.MethodMetrics> entry : metricsAggregatorBean.getAllMetrics().entrySet()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("invocations", entry.getValue().getInvocationCount());
            stats.put("avgTimeMs", Math.round(entry.getValue().getAvgDurationMs() * 100.0) / 100.0);
            stats.put("maxTimeMs", Math.round(entry.getValue().getMaxDurationMs() * 100.0) / 100.0);
            metricsData.put(entry.getKey(), stats);
        }

        response.put("metrics", metricsData);
        return response;
    }

}
