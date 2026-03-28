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
          case COMPANY -> searchCompanies(query, page, size);
          case CONTEST -> searchContests(query, page, size);
          case PROJECT -> searchProjects(query, page, size);
        };

    var count =
        SearchResponse.Count.builder()
            .userCount(profileService.countUsers(query))
            .schoolCount(providerService.countProviders(ProviderType.SCHOOL, query))
            .companyCount(providerService.countProviders(ProviderType.COMPANY, query))
            .contestCount(providerService.countProviders(ProviderType.CONTEST, query))
            .projectCount(providerService.countProviders(ProviderType.PROJECT, query))
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

  public Page<SearchResponse.Result> searchCompanies(String query, int page, int size) {
    log.debug("Searching for companies with query '{}', page {}, size {}", query, page, size);

    Page<Provider> companies =
        providerService.searchProviders(ProviderType.COMPANY, query, page, size);

    log.debug("Found {} companies matching query '{}'", companies.getTotalElements(), query);

    return companies.map(
        company ->
            SearchResponse.Result.builder().id(company.getId()).name(company.getName()).build());
  }

  public Page<SearchResponse.Result> searchContests(String query, int page, int size) {
    log.debug("Searching for contests with query '{}', page {}, size {}", query, page, size);

    Page<Provider> contests =
        providerService.searchProviders(ProviderType.CONTEST, query, page, size);

    log.debug("Found {} contests matching query '{}'", contests.getTotalElements(), query);

    return contests.map(
        contest ->
            SearchResponse.Result.builder().id(contest.getId()).name(contest.getName()).build());
  }

  public Page<SearchResponse.Result> searchProjects(String query, int page, int size) {
    log.debug("Searching for projects with query '{}', page {}, size {}", query, page, size);

    Page<Provider> projects =
        providerService.searchProviders(ProviderType.PROJECT, query, page, size);

    log.debug("Found {} projects matching query '{}'", projects.getTotalElements(), query);

    return projects.map(
        project ->
            SearchResponse.Result.builder().id(project.getId()).name(project.getName()).build());
  }
}
