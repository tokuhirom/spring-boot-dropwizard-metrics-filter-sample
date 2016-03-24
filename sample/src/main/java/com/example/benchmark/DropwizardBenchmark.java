package com.example.benchmark;

import me.geso.nanobench.Benchmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by tokuhirom on 3/24/16.
 */
@SpringBootApplication
public class DropwizardBenchmark {
    public static void main(String[] args) throws Exception {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(DropwizardBenchmark.class)
                .web(false)
                .run(args)) {
            DropwizardBenchmark bean = context.getBean(DropwizardBenchmark.class);
            bean.doMain();
        }
    }

    @Autowired
    private B b;

    private void doMain() throws Exception {
        Benchmark benchmark = new Benchmark(b);
        benchmark.runByTime(1)
                .timethese()
                .cmpthese();
    }

    @Component
    public static class B {
        @Autowired
        private GaugeService gaugeService;
        @Autowired
        private DropwizardMetricServices dropwizardMetricServices;

        @Benchmark.Bench
        public void benchHistogram() {
            gaugeService.submit("histogram.x", 3.14);
        }

        @Benchmark.Bench
        public void benchTimer() {
            gaugeService.submit("timer.x", 3.14);
        }

        @Benchmark.Bench
        public void benchMetrics() {
            dropwizardMetricServices.increment("metrics.x");
        }
    }
}
