package com.skkil.sync.config;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

  private static final int CORE_POOL_SIZE = 4;
  private static final int MAX_POOL_SIZE = 4;
  private static final int QUEUE_CAPACITY = 200;

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(CORE_POOL_SIZE);
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    executor.setQueueCapacity(QUEUE_CAPACITY);
    executor.setThreadNamePrefix("async-event-");
    executor.initialize();
    return executor;
  }

  @Override
  public @Nullable AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, objects) -> {
      log.error("Exception message - " + throwable.getMessage());
      log.info("Method name - " + method.getName());
      for (Object param : objects) {
        log.info("Parameter value - " + param);
      }
    };
  }
}
