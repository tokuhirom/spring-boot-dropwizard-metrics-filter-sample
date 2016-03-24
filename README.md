# spring-boot + dropwizard metrics filter sample

Use dropwizard metrics' great web application instrumented filter with spring boot.

## Sample output

```
{
   "classes" : 5922,
   "classes.loaded" : 5922,
   "classes.unloaded" : 0,
   "counter.status.200.hello.id" : 1000,
   "counter.status.200.metrics" : 3,
   "counter.status.404.star-star" : 1000,
   "gauge.response.hello.id" : 3,
   "gauge.response.metrics" : 24,
   "gauge.response.star-star" : 105,
   "gc.ps_marksweep.count" : 1,
   "gc.ps_marksweep.time" : 85,
   "gc.ps_scavenge.count" : 9,
   "gc.ps_scavenge.time" : 151,
   "heap" : 1864192,
   "heap.committed" : 274944,
   "heap.init" : 131072,
   "heap.used" : 205912,
   "httpsessions.active" : 0,
   "httpsessions.max" : -1,
   "instance.uptime" : 52108,
   "instrumented.activeRequests" : 1,
   "instrumented.requests.count" : 2003,
   "instrumented.requests.fifteenMinuteRate" : 2.2068657173426,
   "instrumented.requests.fiveMinuteRate" : 6.51051261011083,
   "instrumented.requests.meanRate" : 37.8061917507824,
   "instrumented.requests.oneMinuteRate" : 29.5187887307098,
   "instrumented.requests.snapshot.75thPercentile" : 4,
   "instrumented.requests.snapshot.95thPercentile" : 18,
   "instrumented.requests.snapshot.98thPercentile" : 40,
   "instrumented.requests.snapshot.999thPercentile" : 135,
   "instrumented.requests.snapshot.99thPercentile" : 72,
   "instrumented.requests.snapshot.max" : 155,
   "instrumented.requests.snapshot.mean" : 5,
   "instrumented.requests.snapshot.median" : 2,
   "instrumented.requests.snapshot.min" : 0,
   "instrumented.requests.snapshot.stdDev" : 12,
   "instrumented.responseCodes.badRequest.count" : 0,
   "instrumented.responseCodes.badRequest.fifteenMinuteRate" : 0,
   "instrumented.responseCodes.badRequest.fiveMinuteRate" : 0,
   "instrumented.responseCodes.badRequest.meanRate" : 0,
   "instrumented.responseCodes.badRequest.oneMinuteRate" : 0,
   "instrumented.responseCodes.created.count" : 0,
   "instrumented.responseCodes.created.fifteenMinuteRate" : 0,
   "instrumented.responseCodes.created.fiveMinuteRate" : 0,
   "instrumented.responseCodes.created.meanRate" : 0,
   "instrumented.responseCodes.created.oneMinuteRate" : 0,
   "instrumented.responseCodes.noContent.count" : 0,
   "instrumented.responseCodes.noContent.fifteenMinuteRate" : 0,
   "instrumented.responseCodes.noContent.fiveMinuteRate" : 0,
   "instrumented.responseCodes.noContent.meanRate" : 0,
   "instrumented.responseCodes.noContent.oneMinuteRate" : 0,
   "instrumented.responseCodes.notFound.count" : 1000,
   "instrumented.responseCodes.notFound.fifteenMinuteRate" : 1.1078032687181,
   "instrumented.responseCodes.notFound.fiveMinuteRate" : 3.3036876086696,
   "instrumented.responseCodes.notFound.meanRate" : 18.8739613014361,
   "instrumented.responseCodes.notFound.oneMinuteRate" : 15.9438096464588,
   "instrumented.responseCodes.ok.count" : 1003,
   "instrumented.responseCodes.ok.fifteenMinuteRate" : 1.09906858728133,
   "instrumented.responseCodes.ok.fiveMinuteRate" : 3.20687964000899,
   "instrumented.responseCodes.ok.meanRate" : 18.929889559489,
   "instrumented.responseCodes.ok.oneMinuteRate" : 13.5762576633774,
   "instrumented.responseCodes.other.count" : 0,
   "instrumented.responseCodes.other.fifteenMinuteRate" : 0,
   "instrumented.responseCodes.other.fiveMinuteRate" : 0,
   "instrumented.responseCodes.other.meanRate" : 0,
   "instrumented.responseCodes.other.oneMinuteRate" : 0,
   "instrumented.responseCodes.serverError.count" : 0,
   "instrumented.responseCodes.serverError.fifteenMinuteRate" : 0,
   "instrumented.responseCodes.serverError.fiveMinuteRate" : 0,
   "instrumented.responseCodes.serverError.meanRate" : 0,
   "instrumented.responseCodes.serverError.oneMinuteRate" : 0,
   "mem" : 325332,
   "mem.free" : 69031,
   "nonheap" : 0,
   "nonheap.committed" : 51392,
   "nonheap.init" : 2496,
   "nonheap.used" : 50388,
   "processors" : 4,
   "systemload.average" : 3.869140625,
   "threads" : 24,
   "threads.daemon" : 22,
   "threads.peak" : 24,
   "threads.totalStarted" : 28,
   "uptime" : 60801
}
```

## Configuration

Here is a sample code.

```
package com.example.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.AbstractInstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class InstrumentedFilterConfig {
    @Bean
    public FilterRegistrationBean instrumentedFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MyInstrumentedFilter());
        registration.addInitParameter("name-prefix", "instrumented");
        return registration;
    }

    public static class MyInstrumentedFilter extends AbstractInstrumentedFilter {
        private static final String NAME_PREFIX = "responseCodes.";
        private static final int OK = 200;
        private static final int CREATED = 201;
        private static final int NO_CONTENT = 204;
        private static final int BAD_REQUEST = 400;
        private static final int NOT_FOUND = 404;
        private static final int SERVER_ERROR = 500;

        /**
         * Creates a new instance of the filter.
         */
        public MyInstrumentedFilter() {
            super(InstrumentedFilter.REGISTRY_ATTRIBUTE,
                    createMeterNamesByStatusCode(),
                    NAME_PREFIX + "other");
        }

        private static Map<Integer, String> createMeterNamesByStatusCode() {
            final Map<Integer, String> meterNamesByStatusCode = new HashMap<Integer, String>(6);
            meterNamesByStatusCode.put(OK, NAME_PREFIX + "ok");
            meterNamesByStatusCode.put(CREATED, NAME_PREFIX + "created");
            meterNamesByStatusCode.put(NO_CONTENT, NAME_PREFIX + "noContent");
            meterNamesByStatusCode.put(BAD_REQUEST, NAME_PREFIX + "badRequest");
            meterNamesByStatusCode.put(NOT_FOUND, NAME_PREFIX + "notFound");
            meterNamesByStatusCode.put(SERVER_ERROR, NAME_PREFIX + "serverError");
            return meterNamesByStatusCode;
        }
    }

    // Pass MetricRegistry configured by Spring boot to InstrumentedFilter.
    @Bean
    public MyListener myListener() {
        return new MyListener();
    }

    public static class MyListener extends InstrumentedFilterContextListener {
        @Autowired
        private MetricRegistry metricRegistry;

        @Override
        protected MetricRegistry getMetricRegistry() {
            return metricRegistry;
        }
    }
}
```

## FAQ

### Why don't you use MetricsFilter?

MetricsFilter don't provide metrics data such as above.

### Is Dropwizard's metrics fast enough?

Yes. It's really fast.

```
Score:

benchHistogram:  1 wallclock secs ( 1.12 usr +  0.08 sys =  1.20 CPU) @ 3801478.48/s (n=4573152)
benchTimer:  1 wallclock secs ( 1.02 usr +  0.01 sys =  1.03 CPU) @ 3164606.01/s (n=3264693)
benchMetrics:  1 wallclock secs ( 1.15 usr +  0.04 sys =  1.19 CPU) @ 25211790.53/s (n=30020990)

Comparison chart:

                        Rate  benchHistogram  benchTimer  benchMetrics
  benchHistogram   3801478/s              --         20%          -85%
      benchTimer   3164606/s            -17%          --          -87%
    benchMetrics  25211791/s            563%        697%            --
```

