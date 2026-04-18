package com.skkil.sync.provider.repository;

import static com.skkil.sync.jooq.tables.Maintainers.MAINTAINERS;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.data.ProviderDto;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderQueryRepository {

  private final DSLContext dsl;

  public ProviderQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<ProviderDto> getVerifiedProvidersAndByTypes(
      Long requesterId, List<ProviderType> types) {
    return (condition, orderFields, size) -> {
      var base = getProviders(requesterId);
      return base.fetch(
          condition.and(PROVIDERS.VERIFIED_BY.isNotNull()).and(PROVIDERS.PROVIDER_TYPE.in(types)),
          orderFields,
          size);
    };
  }

  public CursorPaginationDataFetcher<ProviderDto> getUnverifiedProviders(Long requesterId) {
    return (condition, orderFields, size) -> {
      var base = getProviders(requesterId);
      return base.fetch(condition.and(PROVIDERS.VERIFIED_BY.isNull()), orderFields, size);
    };
  }

  public CursorPaginationDataFetcher<ProviderDto> getMaintainingProviders(Long requesterId) {
    return (condition, orderFields, size) -> {
      var base = getProviders(requesterId);
      return base.fetch(condition.and(MAINTAINERS.USER_ID.isNotNull()), orderFields, size);
    };
  }

  private CursorPaginationDataFetcher<ProviderDto> getProviders(Long requesterId) {
    return (condition, orderFields, size) ->
        dsl.select(
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
}
