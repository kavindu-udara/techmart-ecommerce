package com.techmart.monitoring;

import javax.ejb.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MetricsAggregatorBean {
    // Thread-safe map to store metrics per method
    private final Map<String, MethodMetrics> metricsMap = new ConcurrentHashMap<String, MethodMetrics>();

    @Lock(LockType.READ)
    public void recordExecution(String methodName, long durationNanos) {
        metricsMap.computeIfAbsent(methodName, k -> new MethodMetrics()).record(durationNanos);
    }

    public static class MethodMetrics {
        private final LongAdder invocationCount = new LongAdder();
        private final LongAdder totalDurationNanos = new LongAdder();
        private volatile long maxDurationNanos = 0;

        // Synchronized only for the max calculation to ensure thread safety
        public synchronized void record(long durationNanos) {
            invocationCount.increment();
            totalDurationNanos.add(durationNanos);
            if (durationNanos > maxDurationNanos) {
                maxDurationNanos = durationNanos;
            }
        }

        public long getInvocationCount() {
            return invocationCount.sum();
        }

        public double getAvgDurationMs() {
            long count = invocationCount.sum();
            return count == 0 ? 0 : (totalDurationNanos.sum() / 1_000_000.0) / count;
        }

        public double getMaxDurationMs() {
            return maxDurationNanos / 1_000_000.0;
        }

    }
}
