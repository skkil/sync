package com.skkil.sync.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.company.model.Company;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.contest.model.Contest;
import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.service.ProviderService;
import com.skkil.sync.search.dto.response.SearchResponse;
import com.skkil.sync.search.enums.SearchType;
import com.skkil.sync.user.service.ProfileService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class SearchServiceTests {

  @Mock private ProfileService profileService;
  @Mock private ProviderService providerService;
  @Mock private CursorConverter cursorConverter;

  private SearchService searchService;

  @BeforeEach
  void setUp() {
    searchService =
        new SearchService(profileService, providerService, new PaginationService(cursorConverter));
  }

  @Test
  @DisplayName("[search] 기업 검색 시 기업 결과와 전체 카운트를 반환")
  void search_company_returnCompanyResultsAndCounts() {
    String query = "sync";
    Company company =
        Company.builder().name("Sync").description("description").industry("IT").build();
    company.setId(1L);

    when(providerService.searchProviders(ProviderType.COMPANY, query, 0, 10))
        .thenReturn(new PageImpl<>(List.of(company)));
    stubCounts(query);

    SearchResponse response = searchService.search(query, SearchType.COMPANY, pagination());

    assertThat(response.results().content())
        .singleElement()
        .extracting(SearchResponse.Result::id, SearchResponse.Result::name)
        .containsExactly(1L, "Sync");
    assertThat(response.count().userCount()).isEqualTo(5L);
    assertThat(response.count().schoolCount()).isEqualTo(4L);
    assertThat(response.count().companyCount()).isEqualTo(3L);
    assertThat(response.count().contestCount()).isEqualTo(2L);
    assertThat(response.count().projectCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("[search] 대회 검색 시 대회 결과를 반환")
  void search_contest_returnContestResults() {
    String query = "hackathon";
    Contest contest = Contest.builder().name("Sync Hackathon").description("description").build();
    contest.setId(2L);

    when(providerService.searchProviders(ProviderType.CONTEST, query, 0, 10))
        .thenReturn(new PageImpl<>(List.of(contest)));
    stubCounts(query);

    SearchResponse response = searchService.search(query, SearchType.CONTEST, pagination());

    assertThat(response.results().content())
        .singleElement()
        .extracting(SearchResponse.Result::id, SearchResponse.Result::name)
        .containsExactly(2L, "Sync Hackathon");
  }

  @Test
  @DisplayName("[search] 프로젝트 검색 시 프로젝트 결과를 반환")
  void search_project_returnProjectResults() {
    String query = "platform";
    Project project = Project.builder().name("Sync Platform").description("description").build();
    project.setId(3L);

    when(providerService.searchProviders(ProviderType.PROJECT, query, 0, 10))
        .thenReturn(new PageImpl<>(List.of(project)));
    stubCounts(query);

    SearchResponse response = searchService.search(query, SearchType.PROJECT, pagination());

    assertThat(response.results().content())
        .singleElement()
        .extracting(SearchResponse.Result::id, SearchResponse.Result::name)
        .containsExactly(3L, "Sync Platform");
  }

  private void stubCounts(String query) {
    when(profileService.countUsers(query)).thenReturn(5L);
    when(providerService.countProviders(ProviderType.SCHOOL, query)).thenReturn(4L);
    when(providerService.countProviders(ProviderType.COMPANY, query)).thenReturn(3L);
    when(providerService.countProviders(ProviderType.CONTEST, query)).thenReturn(2L);
    when(providerService.countProviders(ProviderType.PROJECT, query)).thenReturn(1L);
  }

  private OffsetPaginationRequest pagination() {
    return new OffsetPaginationRequest(0, 10);
  }
}
