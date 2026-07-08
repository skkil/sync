package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.dto.response.SearchUsersResponse;
import com.skkil.sync.user.service.UserSearchService;
import com.skkil.sync.user.snippets.SearchUsersResponseSnippets;
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

@WebMvcTest(UserSearchController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class UserSearchControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserSearchService userSearchService;

  @Test
  @DisplayName("[searchUsers] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchUsers() throws Exception {
    String query = "skkil";
    SearchUsersResponse response = SearchUsersResponseSnippets.getSearchUsersResponse();

    when(userSearchService.searchUsers(eq(query))).thenReturn(response);

    mockMvc
        .perform(get("/search/users").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchUsers",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Search Users")
                    .description("검색어로 사용자를 검색합니다.")
                    .responseSchema(schema(SearchUsersResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("query").description("검색어")),
                SearchUsersResponseSnippets.getSearchUsersResponseFields()));
  }

  @Test
  @DisplayName("[searchUsers] 로그인하지 않은 사용자는 접근할 수 없다")
  void searchUsers_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/search/users").queryParam("query", "skkil"))
        .andExpect(status().isUnauthorized());
  }
}
