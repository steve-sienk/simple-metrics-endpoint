package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.apache.geode.metrics.MetricsPublishingService;
import org.apache.geode.metrics.MetricsSession;

public class MyMetricsPublishingService implements MetricsPublishingService {
  private MetricsSession session;
  private PrometheusMeterRegistry registry;
  private HttpServer httpServer;

  @Override
  public void start(MetricsSession session) {
    this.session = session;
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    this.session.addSubregistry(registry);

    InetSocketAddress address = new InetSocketAddress(9000);
    httpServer = null;
    try {
      httpServer = HttpServer.create(address, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }
    HttpContext context = httpServer.createContext("/");
    context.setHandler(this::requestHandler);
    httpServer.start();
  }

  private void requestHandler(HttpExchange httpExchange) throws IOException {
    final byte[] scrapeBytes = registry.scrape().getBytes();
    httpExchange.sendResponseHeaders(200, scrapeBytes.length);
    final OutputStream responseBody = httpExchange.getResponseBody();
    responseBody.write(scrapeBytes);
    responseBody.close();
  }

  @Override
  public void stop() {
    session.removeSubregistry(registry);
    httpServer.stop(0);
  }
}
