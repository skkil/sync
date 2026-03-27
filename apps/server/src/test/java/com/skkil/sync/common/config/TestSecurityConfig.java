package com.skkil.sync.common.config;

import com.skkil.sync.common.security.GlobalPermissionEvaluator;
import java.util.Collections;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class TestSecurityConfig {

  @Bean
  UserDetailsService userDetailsService() {
    return username -> null;
  }

  @Bean
  GlobalPermissionEvaluator globalPermissionEvaluator() {
    return new GlobalPermissionEvaluator(Collections.emptyList());
  }
}
