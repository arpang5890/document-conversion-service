package com.document.conversion.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class MetricsService {

    private final Counter conversionRequestsTotal;
    private final Counter conversionSuccessTotal;
    private final Counter conversionFailureTotal;
    private final AtomicInteger activeConversions;
    private final Timer conversionDuration;

    public MetricsService(MeterRegistry registry) {
        this.conversionRequestsTotal = Counter.builder("document_conversion_requests_total")
                .description("Total number of conversion requests")
                .register(registry);

        this.conversionSuccessTotal = Counter.builder("document_conversion_success_total")
                .description("Total number of successful conversions")
                .register(registry);

        this.conversionFailureTotal = Counter.builder("document_conversion_failure_total")
                .description("Total number of failed conversions")
                .register(registry);

        this.activeConversions = registry.gauge("document_conversion_active",
                new AtomicInteger(0));

        this.conversionDuration = Timer.builder("document_conversion_duration")
                .description("Time taken for document conversion")
                .register(registry);
    }

    public void recordConversionStart() {
        conversionRequestsTotal.increment();
        activeConversions.incrementAndGet();
    }

    public void recordConversionSuccess() {
        conversionSuccessTotal.increment();
        activeConversions.decrementAndGet();
    }

    public void recordConversionFailure() {
        conversionFailureTotal.increment();
        activeConversions.decrementAndGet();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample sample) {
        if (sample != null) {
            sample.stop(conversionDuration);
        }
    }
}
