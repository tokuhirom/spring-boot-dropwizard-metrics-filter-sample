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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Aggregate request time histogram.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RichMetricsFilter extends OncePerRequestFilter {
    private CounterService counterService;
    private final GaugeService gaugeService;
    private final List<String> recordingPatterns;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final List<PatternReplacer> STATUS_REPLACERS;

    static {
        List<PatternReplacer> replacements = new ArrayList<>();
        replacements.add(new PatternReplacer("[{}]", 0, "-"));
        replacements.add(new PatternReplacer("**", Pattern.LITERAL, "-star-star-"));
        replacements.add(new PatternReplacer("*", Pattern.LITERAL, "-star-"));
        replacements.add(new PatternReplacer("/-", Pattern.LITERAL, "/"));
        replacements.add(new PatternReplacer("-/", Pattern.LITERAL, "/"));
        STATUS_REPLACERS = Collections.unmodifiableList(replacements);
    }

    private static final List<PatternReplacer> KEY_REPLACERS;

    static {
        List<PatternReplacer> replacements = new ArrayList<PatternReplacer>();
        replacements.add(new PatternReplacer("/", Pattern.LITERAL, "."));
        replacements.add(new PatternReplacer("src/main", Pattern.LITERAL, "."));
        KEY_REPLACERS = replacements;
    }
    
    private static final Log logger = LogFactory.getLog(RichMetricsFilter.class);

    public RichMetricsFilter(CounterService counterService, GaugeService gaugeService) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
        this.recordingPatterns = Arrays.asList();
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

                counterService.increment("status." + status);
                if (pattern != null) {
                    counterService.increment("status." + status + "." + pattern);
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
            for (String recordingPattern : recordingPatterns) {
                if (antPathMatcher.match(recordingPattern, path)) {
                    return getKey(fixSpecialCharacters(recordingPattern));
                }
            }
        }
        return null;
    }

    private String getKey(String string) {
        // graphite compatible metric names
        String key = string;
        for (PatternReplacer replacer : KEY_REPLACERS) {
            key = replacer.apply(key);
        }
        if (key.endsWith(".")) {
            key = key + "root";
        }
        if (key.startsWith("_")) {
            key = key.substring(1);
        }
        return key;
    }

    private String fixSpecialCharacters(String value) {
        String result = value;
        for (PatternReplacer replacer : STATUS_REPLACERS) {
            result = replacer.apply(result);
        }
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        if (result.startsWith("-")) {
            result = result.substring(1);
        }
        return result;
    }

    private static class PatternReplacer {
        private final Pattern pattern;

        private final String replacement;

        PatternReplacer(String regex, int flags, String replacement) {
            this.pattern = Pattern.compile(regex, flags);
            this.replacement = replacement;
        }

        String apply(String input) {
            return this.pattern.matcher(input)
                    .replaceAll(Matcher.quoteReplacement(this.replacement));
        }
    }

}
