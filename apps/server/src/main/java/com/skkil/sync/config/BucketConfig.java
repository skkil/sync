package com.skkil.sync.config;

import com.skkil.sync.common.exception.ErrorCode;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.jdbc.PrimaryKeyMapper;
import io.github.bucket4j.distributed.proxy.ExpiredEntriesCleaner;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.postgresql.Bucket4jPostgreSQL;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Slf4j
public class BucketConfig {

  private static final int MAX_EXPIRED_BUCKETS_PER_RUN = 1000;
  private static final int AUTH_RATE_LIMIT_CAPACITY = 10;
  private static final Duration AUTH_RATE_LIMIT_REFILL_PERIOD = Duration.ofMinutes(5);

  private final DataSource dataSource;

  public BucketConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Bean
  ProxyManager<String> rateLimitProxyManager() {
    return Bucket4jPostgreSQL.selectForUpdateBasedBuilder(dataSource)
        .primaryKeyMapper(PrimaryKeyMapper.STRING)
        .expirationAfterWrite(
            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                AUTH_RATE_LIMIT_REFILL_PERIOD.multipliedBy(2)))
        .build();
  }

  @Bean
  FilterRegistrationBean<RateLimitFilter> authRateLimitFilter() {
    FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new RateLimitFilter(rateLimitProxyManager()));
    registration.addUrlPatterns(
        "/auth/login",
        "/auth/register",
        "/auth/email-verification/send",
        "/auth/email-verification/verify");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return registration;
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
  void removeExpiredBuckets() {
    if (!(rateLimitProxyManager() instanceof ExpiredEntriesCleaner cleaner)) {
      return;
    }

    int removed = cleaner.removeExpired(MAX_EXPIRED_BUCKETS_PER_RUN);
    if (removed > 0) {
      log.debug("Removed {} expired rate limit buckets", removed);
    }
  }

  private static class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;
    private final JsonMapper jsonMapper = new JsonMapper();

    RateLimitFilter(ProxyManager<String> proxyManager) {
      this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
      String key = request.getRequestURI() + ":" + request.getRemoteAddr();
      Bucket bucket = proxyManager.getProxy(key, this::bucketConfiguration);

      ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
      if (probe.isConsumed()) {
        chain.doFilter(request, response);
        return;
      }

      long retryAfterSeconds =
          Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));

      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
      response.setCharacterEncoding("UTF-8");
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      ProblemDetail problemDetail =
          ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests");
      problemDetail.setProperty("code", ErrorCode.RATE_LIMIT_EXCEEDED);

      response.getWriter().write(jsonMapper.writeValueAsString(problemDetail));
    }

    private BucketConfiguration bucketConfiguration() {
      return BucketConfiguration.builder()
          .addLimit(
              limit ->
                  limit
                      .capacity(AUTH_RATE_LIMIT_CAPACITY)
                      .refillGreedy(AUTH_RATE_LIMIT_CAPACITY, AUTH_RATE_LIMIT_REFILL_PERIOD))
          .build();
    }
  }
}
