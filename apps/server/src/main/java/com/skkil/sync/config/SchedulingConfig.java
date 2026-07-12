package com.skkil.sync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

  private static final int POOL_SIZE = 4;

  @Override
  public void configureTasks(ScheduledTaskRegistrar registrar) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(POOL_SIZE);
    scheduler.setThreadNamePrefix("scheduled-task-");
    scheduler.setAwaitTerminationSeconds(30);
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.initialize();
    registrar.setTaskScheduler(scheduler);
  }
}
