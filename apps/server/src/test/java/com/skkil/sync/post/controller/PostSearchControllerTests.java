package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.service.PostSearchService;
import com.skkil.sync.post.snippets.SearchPostsResponseSnippets;
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

@WebMvcTest(PostSearchController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostSearchControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostSearchService postSearchService;

  @Test
  @DisplayName("[searchPosts] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchPosts() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String query = "test query";
    SearchPostsResponse response = SearchPostsResponseSnippets.getSearchPostsResponse();

    when(postSearchService.searchPosts(eq(user.userId()), eq(query), isNull()))
        .thenReturn(response);

    mockMvc
        .perform(get("/search/posts").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchPosts",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Search Posts")
                    .description("Search Posts by query string")
                    .responseSchema(schema("SearchPostsResponse")),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("query").description("Search query (1-100 characters)"),
                    parameterWithName("projectHandle")
                        .description("프로젝트로 검색 범위 제한 (선택)")
                        .optional()),
                SearchPostsResponseSnippets.getSearchPostsResponseFields()));
  }

  @Test
  @DisplayName("[searchPosts] 로그인하지 않은 사용자는 접근할 수 없다")
  void searchPosts_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/search/posts").queryParam("query", "test query"))
        .andExpect(status().isUnauthorized());
  }
}
