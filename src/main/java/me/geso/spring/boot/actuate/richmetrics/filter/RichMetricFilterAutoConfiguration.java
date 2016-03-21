package me.geso.spring.boot.actuate.richmetrics.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

@Configuration
// @ConditionalOnBean({CounterService.class, GaugeService.class})
@ConditionalOnClass({Servlet.class, ServletRegistration.class,
        OncePerRequestFilter.class, HandlerMapping.class})
@AutoConfigureAfter(MetricRepositoryAutoConfiguration.class)
// @ConditionalOnProperty(name = "richmetrics.filter.enabled", matchIfMissing = true)
@Import(RichMetricsProperties.class)
@EnableConfigurationProperties(RichMetricsProperties.class)
public class RichMetricFilterAutoConfiguration {
    @Autowired
    private GaugeService gaugeService;
    @Autowired
    private CounterService counterService;
    @Autowired
    private RichMetricsProperties richMetricsProperties;

    public RichMetricFilterAutoConfiguration() {
    }

    @Bean
    public RichMetricsFilter histogramMetricsFilter() {
        System.out.println(richMetricsProperties);
        return new RichMetricsFilter(counterService, gaugeService);
    }
}
