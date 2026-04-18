package com.skkil.sync.provider.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.service.ProviderQueryService;
import com.skkil.sync.provider.snippets.GetProvidersResponseSnippets;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@WebMvcTest(ProviderQueryController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProviderQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProviderQueryService providerQueryService;

  @Test
  @DisplayName("[searchProviders] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchProviders() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetProvidersResponse response = GetProvidersResponseSnippets.getGetProvidersResponse();

    String query = "query";
    List<ProviderType> types = List.of(ProviderType.COMPANY);

    MultiValueMap<String, String> queryParams =
        CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams();
    queryParams.add("query", query);
    queryParams.addAll("types", types.stream().map(Enum::name).toList());

    when(providerQueryService.searchProviders(user.userId(), query, types, pagination))
        .thenReturn(response);

    mockMvc
        .perform(get("/search/providers").queryParams(queryParams))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchProviders",
                ResourceSnippetParameters.builder()
                    .tag("providers")
                    .summary("Search Providers")
                    .description("Search Providers")
                    .responseSchema(schema(GetProvidersResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters()
                    .and(
                        parameterWithName("query").description("Search Query").optional(),
                        parameterWithName("types").description("Provider Types List").optional()),
                GetProvidersResponseSnippets.getGetProvidersResponseFieldsSnippet()));
  }

  @Test
  @DisplayName("[getMyProviders] API 문서화 테스트")
  @WithAuthenticatedUser
  void getMyProviders() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetProvidersResponse response = GetProvidersResponseSnippets.getGetProvidersResponse();

    when(providerQueryService.getMyProviders(user.userId(), pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/providers/my")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetMyProviders",
                ResourceSnippetParameters.builder()
                    .tag("providers")
                    .summary("Get My Providers")
                    .description("Get My Providers")
                    .responseSchema(schema(GetProvidersResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetProvidersResponseSnippets.getGetProvidersResponseFieldsSnippet()));
  }

  @Test
  @DisplayName("[getProviderTypes] 로그인하지 않은 사용자는 접근할 수 없다")
  void getMyProviders_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            get("/providers/my")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isUnauthorized());
  }
}
