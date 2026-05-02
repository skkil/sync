package com.skkil.sync.provider.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
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
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.service.ProviderQueryService;
import com.skkil.sync.provider.snippets.GetProvidersResponseSnippets;
import com.skkil.sync.user.constant.Role;
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

@WebMvcTest(AdminProviderQueryController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class AdminProviderQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProviderQueryService providerQueryService;

  @Test
  @DisplayName("[getUnverifiedProviders] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void getUnverifiedProviders() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetProvidersResponse response = GetProvidersResponseSnippets.getGetProvidersResponse();

    when(providerQueryService.getUnverifiedProviders(user.userId(), pagination))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/admin/providers/unverified")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetUnverifiedProviders",
                ResourceSnippetParameters.builder()
                    .tag("providers")
                    .summary("Get Unverified Providers")
                    .description("Get Unverified Providers")
                    .responseSchema(schema(GetProvidersResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetProvidersResponseSnippets.getGetProvidersResponseFieldsSnippet()));
  }

  @Test
  @DisplayName("[getUnverifiedProviders] 로그인하지 않은 사용자는 접근할 수 없다")
  void getUnverifiedProviders_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            get("/admin/providers/unverified")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getUnverifiedProviders] ADMIN 권한이 없는 사용자는 접근할 수 없다")
  @WithAuthenticatedUser(role = Role.USER)
  void getUnverifiedProviders_nonAdminUser_shouldReturnForbidden() throws Exception {
    mockMvc
        .perform(
            get("/admin/providers/unverified")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isForbidden());
  }
}
