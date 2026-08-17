package com.skkil.sync.config;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
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

  private static final int EMAIL_CORE_POOL_SIZE = 2;
  private static final int EMAIL_MAX_POOL_SIZE = 8;
  private static final int EMAIL_QUEUE_CAPACITY = 50;

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(CORE_POOL_SIZE);
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    executor.setQueueCapacity(QUEUE_CAPACITY);
    executor.setThreadNamePrefix("async-event-");
    executor.setRejectedExecutionHandler(this::logRejectedTask);
    executor.initialize();
    return executor;
  }

  /**
   * Dedicated pool for {@code EmailService}, kept separate from {@link #getAsyncExecutor()} so a
   * stuck SMTP call (e.g. a hung DNS lookup, which JavaMail's connect timeout does not cover)
   * cannot exhaust the pool that event listeners and other {@code @Async} work depend on. Sized
   * above core so new sends still get a thread while a previously wedged one sits blocked.
   */
  @Bean("emailTaskExecutor")
  public ThreadPoolTaskExecutor emailTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(EMAIL_CORE_POOL_SIZE);
    executor.setMaxPoolSize(EMAIL_MAX_POOL_SIZE);
    executor.setQueueCapacity(EMAIL_QUEUE_CAPACITY);
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(60);
    executor.setThreadNamePrefix("async-email-");
    executor.setRejectedExecutionHandler(this::logRejectedTask);
    executor.initialize();
    return executor;
  }

  private void logRejectedTask(Runnable task, ThreadPoolExecutor executor) {
    log.error(
        "Async task rejected, pool exhausted (active={}, poolSize={}, queueSize={}): {}",
        executor.getActiveCount(),
        executor.getPoolSize(),
        executor.getQueue().size(),
        task);
    throw new RejectedExecutionException("Async task rejected: " + task);
  }

  @Override
  public @Nullable AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    // Throwable을 SLF4J 마지막 인자로 넘겨야 스택트레이스와 cause 체인이 남는다.
    // 메시지만 이어 붙이면 알림·이메일 등 모든 @Async 실패가 한 줄로 축약되어
    // 원인 추적이 불가능해진다.
    return (throwable, method, objects) ->
        log.error(
            "비동기 메서드 {}.{} 실행 실패 (인자: {})",
            method.getDeclaringClass().getSimpleName(),
            method.getName(),
            Arrays.toString(objects),
            throwable);
  }
}
