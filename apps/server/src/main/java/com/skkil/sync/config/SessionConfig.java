package com.skkil.sync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession(cleanupCron = "0 0 * * * *")
public class SessionConfig {}
