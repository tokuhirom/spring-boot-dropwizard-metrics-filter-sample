package me.geso.spring.boot.actuate.richmetrics.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "richmetrics")
class RichMetricsProperties {
    private Map<String, String> patterns;

    public Map<String, String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Map<String, String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String toString() {
        return "RichMetricsProperties{" +
                "patterns=" + patterns +
                '}';
    }
}
