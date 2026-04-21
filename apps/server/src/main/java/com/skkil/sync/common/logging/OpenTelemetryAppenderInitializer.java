package com.skkil.sync.common.logging;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
class OpenTelemetryAppenderInitializer implements InitializingBean {

  private final OpenTelemetry openTelemetry;

  public OpenTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    OpenTelemetryAppender.install(openTelemetry);
  }
}
