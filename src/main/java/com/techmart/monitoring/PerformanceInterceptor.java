package com.techmart.monitoring;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.logging.Logger;

@Interceptor
@Monitored
public class PerformanceInterceptor implements Serializable {
    private static final Logger logger = Logger.getLogger(PerformanceInterceptor.class.getName());
    // Threshold for "slow" execution (e.g., 500 milliseconds in nanoseconds)
    private static final long SLOW_THRESHOLD_NANOS = 500_000_000L;

    @Inject
    private MetricsAggregatorBean metricsAggregatorBean;

    @AroundInvoke
    public Object monitorPerformance(InvocationContext context) throws Exception {
        String className = context.getTarget().getClass().getSimpleName();
        String methodName = context.getMethod().getName();
        String fullMethodName = className + "." + methodName;

        logger.info("INTERCEPTOR TRIGGERED FOR: " + fullMethodName);
        // Start high-precision timer
        long startTime = System.nanoTime();

        try {
            return context.proceed();
        } catch (Exception e) {
            logger.warning("Exception intercepted in " + fullMethodName + ": " + e.getMessage());
            throw e;
        } finally {
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            // 1. Record the metric for the web dashboard
            metricsAggregatorBean.recordExecution(fullMethodName, duration);

            // 2. Log slow executions to help identify bottlenecks (NFR requirement)
            if (duration > SLOW_THRESHOLD_NANOS) {
                logger.warning("SLOW EXECUTION DETECTED: " + fullMethodName +
                        " took " + (duration / 1_000_000) + " ms");
            }
        }
    }

}
