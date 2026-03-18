package com.skkil.sync.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailConfigDebugRunner implements CommandLineRunner {

  private final Environment environment;

  @Override
  public void run(String... args) {
    log.info("spring.mail.host = {}", environment.getProperty("spring.mail.host"));
    log.info("spring.mail.port = {}", environment.getProperty("spring.mail.port"));
    log.info("spring.mail.username = {}", environment.getProperty("spring.mail.username"));
    log.info(
        "spring.mail.password.exists = {}",
        environment.getProperty("spring.mail.password") != null
            && !environment.getProperty("spring.mail.password").isBlank());
  }
}
