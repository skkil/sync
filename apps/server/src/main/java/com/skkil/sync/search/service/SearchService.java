package com.skkil.sync.search.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.service.ProviderService;
import com.skkil.sync.search.dto.response.SearchResponse;
import com.skkil.sync.search.enums.SearchType;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchService {

  private final ProfileService profileService;
  private final ProviderService providerService;

  public SearchService(ProfileService profileService, ProviderService providerService) {
    this.profileService = profileService;
    this.providerService = providerService;
  }

  public SearchResponse search(String query, SearchType type, int page, int size) {
    var results =
        switch (type) {
          case USER -> searchUsers(query, page, size);
          case SCHOOL -> searchSchools(query, page, size);
        };

    var count =
        SearchResponse.Count.builder()
            .userCount(profileService.countUsers(query))
            .schoolCount(providerService.countProviders(ProviderType.SCHOOL, query))
            .build();

    return new SearchResponse(results, count);
  }

  public Page<SearchResponse.Result> searchUsers(String query, int page, int size) {
    log.debug("Searching for users with query '{}', page {}, size {}", query, page, size);

    Page<User> users = profileService.searchUsers(query, page, size);

    log.debug("Found {} users matching query '{}'", users.getTotalElements(), query);

    return users.map(
        user -> SearchResponse.Result.builder().id(user.getId()).name(user.getFullName()).build());
  }

  public Page<SearchResponse.Result> searchSchools(String query, int page, int size) {
    log.debug("Searching for schools with query '{}', page {}, size {}", query, page, size);

    Page<Provider> schools =
        providerService.searchProviders(ProviderType.SCHOOL, query, page, size);

    log.debug("Found {} schools matching query '{}'", schools.getTotalElements(), query);

    return schools.map(
        school ->
            SearchResponse.Result.builder().id(school.getId()).name(school.getName()).build());
  }
}
