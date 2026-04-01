package com.skkil.sync.provider.service.query;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.data.ProviderDto;
import com.skkil.sync.provider.dto.query.ProviderQuery;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.enums.ProviderVerificationStatus;
import com.skkil.sync.provider.repository.query.ProviderQueryRepository;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderQueryService {

  private final ProviderQueryRepository providerQueryRepository;
  private final CursorConverter cursorConverter;

  public ProviderQueryService(
      ProviderQueryRepository providerQueryRepository, CursorConverter cursorConverter) {
    this.providerQueryRepository = providerQueryRepository;
    this.cursorConverter = cursorConverter;
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse searchProviders(
      Long requesterId,
      String query,
      List<ProviderType> types,
      CursorPaginationRequest pagination) {
    Cursor cursor = cursorConverter.decode(pagination.cursor());
    ProviderQuery providerQuery =
        ProviderQuery.builder()
            .query(query)
            .createdAfter(cursor != null ? cursor.createdAt() : null)
            .afterId(cursor != null ? cursor.id() : null)
            .types(types)
            .verificationStatuses(List.of(ProviderVerificationStatus.VERIFIED))
            .build();

    List<ProviderDto> providers =
        providerQueryRepository.getProviders(requesterId, providerQuery, pagination.size() + 1);

    return mapToGetProvidersResponse(providers, pagination.size());
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse getMyProviders(Long requesterId, CursorPaginationRequest pagination) {
    Cursor cursor = cursorConverter.decode(pagination.cursor());
    ProviderQuery providerQuery =
        ProviderQuery.builder()
            .createdAfter(cursor != null ? cursor.createdAt() : null)
            .afterId(cursor != null ? cursor.id() : null)
            .verificationStatuses(
                List.of(ProviderVerificationStatus.VERIFIED, ProviderVerificationStatus.UNVERIFIED))
            .maintainerId(requesterId)
            .build();

    List<ProviderDto> providers =
        providerQueryRepository.getProviders(requesterId, providerQuery, pagination.size() + 1);

    return mapToGetProvidersResponse(providers, pagination.size());
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse getUnverifiedProviders(
      Long requesterId, CursorPaginationRequest pagination) {
    Cursor cursor = cursorConverter.decode(pagination.cursor());
    ProviderQuery providerQuery =
        ProviderQuery.builder()
            .createdAfter(cursor != null ? cursor.createdAt() : null)
            .afterId(cursor != null ? cursor.id() : null)
            .verificationStatuses(List.of(ProviderVerificationStatus.UNVERIFIED))
            .build();

    List<ProviderDto> providers =
        providerQueryRepository.getProviders(requesterId, providerQuery, pagination.size() + 1);

    return mapToGetProvidersResponse(providers, pagination.size());
  }

  private GetProvidersResponse mapToGetProvidersResponse(List<ProviderDto> providers, int size) {
    boolean hasNext = providers.size() > size;
    Cursor nextCursor = getNextCursor(providers.subList(0, Math.min(providers.size(), size)));

    List<GetProvidersResponse.Provider> content =
        providers.stream()
            .limit(size)
            .map(
                provider ->
                    GetProvidersResponse.Provider.builder()
                        .type(provider.type())
                        .id(provider.id().toString())
                        .name(provider.name())
                        .isVerified(provider.verifiedByUserId() != null)
                        .isMaintainer(provider.isMaintainer())
                        .build())
            .toList();

    return new GetProvidersResponse(
        CursorPaginationResponse.<GetProvidersResponse.Provider>builder()
            .content(content)
            .hasNext(hasNext)
            .nextCursor(cursorConverter.encode(nextCursor))
            .build());
  }

  private Cursor getNextCursor(List<ProviderDto> providers) {
    if (providers.isEmpty()) {
      return null;
    }

    ProviderDto lastProvider = providers.get(providers.size() - 1);
    return Cursor.builder()
        .score(0L)
        .createdAt(lastProvider.createdAt().toInstant(ZoneOffset.UTC))
        .updatedAt(lastProvider.updatedAt().toInstant(ZoneOffset.UTC))
        .id(lastProvider.id())
        .build();
  }
}
