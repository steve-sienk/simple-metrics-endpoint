package org.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.apache.geode.metrics.MetricsPublishingService;
import org.apache.geode.metrics.MetricsSession;

public class MyMetricsPublishingService implements MetricsPublishingService {
  private MetricsSession session;
  private MeterRegistry registry;

  @Override
  public void start(MetricsSession session) {
    this.session = session;
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    this.session.addSubregistry(registry);
  }

  @Override
  public void stop() {
    session.removeSubregistry(registry);
  }
}
