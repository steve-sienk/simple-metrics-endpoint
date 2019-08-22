package org.example;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.geode.metrics.MetricsPublishingService;
import org.apache.geode.metrics.MetricsSession;

@ExtendWith(MockitoExtension.class)
class MyMetricsPublishingServiceTest {

  @Mock
  private MetricsSession metricsSession;

  private MetricsPublishingService subject;

  @BeforeEach
  void setUp() {
    subject = new MyMetricsPublishingService();
  }

  @Test
  void start_addsRegistryToMetricsSession() {
    subject.start(metricsSession);

    verify(metricsSession).addSubregistry(any(PrometheusMeterRegistry.class));
  }

  @Test
  void stop_removesRegistryFromMetricsSession() {
    subject.start(metricsSession);
    subject.stop();

    verify(metricsSession).removeSubregistry(any(PrometheusMeterRegistry.class));
  }
}