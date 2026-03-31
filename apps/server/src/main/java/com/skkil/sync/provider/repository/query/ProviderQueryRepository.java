package com.skkil.sync.provider.repository.query;

import static com.skkil.sync.jooq.tables.Maintainers.MAINTAINERS;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.noCondition;

import com.skkil.sync.provider.dto.data.ProviderDto;
import com.skkil.sync.provider.dto.query.ProviderQuery;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderQueryRepository {

  private final DSLContext dsl;

  public ProviderQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<ProviderDto> getProviders(Long requesterId, ProviderQuery query, int size) {
    Condition condition = buildCursorCondition(query);
    return getProviders(requesterId, condition, size);
  }

  private List<ProviderDto> getProviders(Long requesterId, Condition condition, int size) {
    return dsl.select(
            PROVIDERS.PROVIDER_TYPE,
            PROVIDERS.ID,
            PROVIDERS.NAME,
            PROVIDERS.VERIFIED_BY,
            MAINTAINERS.USER_ID.isNotNull(),
            PROVIDERS.CREATED_AT,
            PROVIDERS.UPDATED_AT)
        .from(PROVIDERS)
        .leftJoin(USERS)
        .on(PROVIDERS.VERIFIED_BY.eq(USERS.ID))
        .leftJoin(MAINTAINERS)
        .on(PROVIDERS.ID.eq(MAINTAINERS.PROVIDER_ID).and(MAINTAINERS.USER_ID.eq(requesterId)))
        .where(condition)
        .orderBy(PROVIDERS.CREATED_AT.asc(), PROVIDERS.ID.asc())
        .limit(size)
        .fetchInto(ProviderDto.class);
  }

  private Condition buildCursorCondition(ProviderQuery query) {
    Condition condition = noCondition();

    if (query.query() != null && !query.query().isBlank()) {
      condition =
          condition.and(
              PROVIDERS
                  .NAME
                  .containsIgnoreCase(query.query())
                  .or(PROVIDERS.DESCRIPTION.containsIgnoreCase(query.query())));
    }

    if (query.createdAfter() != null) {
      condition =
          condition.and(
              PROVIDERS.CREATED_AT.gt(
                  LocalDateTime.ofInstant(query.createdAfter(), ZoneId.systemDefault())));
    }

    if (query.afterId() != null) {
      condition = condition.and(PROVIDERS.ID.gt(query.afterId()));
    }

    if (query.types() != null && !query.types().isEmpty()) {
      condition =
          condition.and(
              PROVIDERS.PROVIDER_TYPE.in(
                  query.types().stream().map(Enum::name).toArray(String[]::new)));
    }

    if (query.verificationStatuses() != null && !query.verificationStatuses().isEmpty()) {
      condition =
          condition.and(
              PROVIDERS.VERIFICATION_STATUS.in(
                  query.verificationStatuses().stream().map(Enum::name).toArray(String[]::new)));
    }

    if (query.maintainerId() != null) {
      condition = condition.and(MAINTAINERS.USER_ID.eq(query.maintainerId()));
    }

    return condition;
  }
}
