package com.skkil.sync.config;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.agent")
public record AgentOAuth2Properties(
    boolean enabled,
    String issuerUri,
    @Nullable String rsaPrivateKey,
    @Nullable String rsaPublicKey,
    @Nullable String chatgptRedirectUri) {}
