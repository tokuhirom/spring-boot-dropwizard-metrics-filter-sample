package me.geso.spring.boot.actuate.richmetrics.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Aggregate request time histogram.
 * See https://github.com/dropwizard/metrics/tree/master/metrics-servlet.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class RichMetricsFilter extends OncePerRequestFilter {
    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final Map<String, String> recordingPatterns;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final Log logger = LogFactory.getLog(RichMetricsFilter.class);

    RichMetricsFilter(CounterService counterService, GaugeService gaugeService, Map<String, String> recordingPatterns) {
        this.counterService = Objects.requireNonNull(counterService);
        this.gaugeService = Objects.requireNonNull(gaugeService);
        // Always process the recording patterns in same order.
        this.recordingPatterns = Collections.unmodifiableMap(new LinkedHashMap<String, String>(recordingPatterns));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            if (!request.isAsyncStarted()) {
                String pattern = getPattern(request);

                long elapsed = System.currentTimeMillis() - start;
                Long contentLength = getContentLength(response);
                int status = response.getStatus();

                counterService.increment("meter.status." + status);
                if (pattern != null) {
                    counterService.increment("meter.status." + status + "." + pattern);
                }

                gaugeService.submit("histogram.request.elapsed", elapsed);
                if (pattern != null) {
                    gaugeService.submit("histogram.request.elapsed." + pattern, elapsed);
                }

                if (contentLength != null) {
                    gaugeService.submit("histogram.request.content_length", contentLength);
                    if (pattern != null) {
                        gaugeService.submit("histogram.request.content_length." + pattern, contentLength);
                    }
                }
            }
        }
    }

    private Long getContentLength(HttpServletResponse response) {
        String header = response.getHeader("content-length");
        if (header != null) {
            try {
                return Long.parseLong(header);
            } catch (NumberFormatException e) {
                logger.info("Non-numeric content-length", e);
            }
        }
        return null;
    }

    private String getPattern(HttpServletRequest request) {
        Object bestMatchingPattern = request
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (bestMatchingPattern != null) {
            String path = bestMatchingPattern.toString();
            for (Map.Entry<String, String> entry : recordingPatterns.entrySet()) {
                if (antPathMatcher.match(entry.getValue(), path)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

}
