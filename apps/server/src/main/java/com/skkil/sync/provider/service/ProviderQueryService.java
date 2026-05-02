package com.skkil.sync.provider.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.mapper.ProviderMapper;
import com.skkil.sync.provider.repository.ProviderQueryRepository;
import com.skkil.sync.provider.repository.pagination.ProviderCursorPaginationProvider;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderQueryService {

  private final ProviderQueryRepository providerQueryRepository;
  private final PaginationService paginationService;
  private final ProviderCursorPaginationProvider paginationProvider;
  private final ProviderMapper providerMapper;

  public ProviderQueryService(
      ProviderQueryRepository providerQueryRepository,
      PaginationService paginationService,
      ProviderCursorPaginationProvider paginationProvider,
      ProviderMapper providerMapper) {
    this.providerQueryRepository = providerQueryRepository;
    this.paginationService = paginationService;
    this.paginationProvider = paginationProvider;
    this.providerMapper = providerMapper;
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse searchProviders(
      Long requesterId,
      String query,
      List<ProviderType> types,
      CursorPaginationRequest pagination) {
    var providers =
        paginationService
            .paginate(
                providerQueryRepository.getVerifiedProvidersAndByTypes(requesterId, types),
                paginationProvider,
                pagination)
            .map(providerMapper::toProviderDto);

    return new GetProvidersResponse(providers);
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse getMyProviders(Long requesterId, CursorPaginationRequest pagination) {
    var providers =
        paginationService
            .paginate(
                providerQueryRepository.getMaintainingProviders(requesterId),
                paginationProvider,
                pagination)
            .map(providerMapper::toProviderDto);

    return new GetProvidersResponse(providers);
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse getUnverifiedProviders(
      Long requesterId, CursorPaginationRequest pagination) {
    var providers =
        paginationService
            .paginate(
                providerQueryRepository.getUnverifiedProviders(requesterId),
                paginationProvider,
                pagination)
            .map(providerMapper::toProviderDto);

    return new GetProvidersResponse(providers);
  }
}
