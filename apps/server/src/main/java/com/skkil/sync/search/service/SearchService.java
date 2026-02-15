package com.skkil.sync.search.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.service.ProviderService;
import com.skkil.sync.search.dto.response.SearchResponse;
import com.skkil.sync.search.enums.SearchType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchService {

  private final ProviderService providerService;

  public SearchService(ProviderService providerService) {
    this.providerService = providerService;
  }

  public SearchResponse search(String query, SearchType type, int page, int size) {
    return switch (type) {
      case USER -> searchUsers(query, page, size);
      case SCHOOL -> searchSchools(query, page, size);
    };
  }

  public SearchResponse searchUsers(String query, int page, int size) {
    log.debug("Searching for users with query '{}', page {}, size {}", query, page, size);

    // TODO: Implement user search logic
    return new SearchResponse(Page.empty());
  }

  public SearchResponse searchSchools(String query, int page, int size) {
    log.debug("Searching for schools with query '{}', page {}, size {}", query, page, size);

    Page<Provider> schools =
        providerService.searchProviders(ProviderType.SCHOOL, query, page, size);

    log.debug("Found {} schools matching query '{}'", schools.getTotalElements(), query);
    schools.forEach(
        school -> log.debug("Found school: id={}, name={}", school.getId(), school.getName()));

    return new SearchResponse(
        schools.map(
            school ->
                SearchResponse.Result.builder().id(school.getId()).name(school.getName()).build()));
  }
}
