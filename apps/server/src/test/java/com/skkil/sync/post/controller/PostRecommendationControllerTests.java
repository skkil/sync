package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.data.PostRecommendationContext;
import com.skkil.sync.post.dto.response.PaginatedGetPostsResponse;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.service.PostRecommendationService;
import com.skkil.sync.post.snippets.PaginatedGetPostsResponseSnippets;
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

@WebMvcTest(PostRecommendationController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostRecommendationControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostRecommendationService postRecommendationService;

  @Test
  @DisplayName("[getRecommendations] API 문서화 테스트")
  @WithAuthenticatedUser
  void getRecommendations() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    PaginatedGetPostsResponse response =
        PaginatedGetPostsResponseSnippets.getPaginatedGetPostsResponse();
    MultiValueMap<String, String> queryParams =
        CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams();

    when(postRecommendationService.getRecommendations(
            eq(new PostRecommendationContext(user.userId(), null, null)), isNull(), any()))
        .thenReturn(response);

    mockMvc
        .perform(get("/posts/recommendations").queryParams(queryParams))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostRecommendations",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Post Recommendations")
                    .description("추천 게시글 목록을 조회합니다.")
                    .responseSchema(schema(PaginatedGetPostsResponse.class.getSimpleName())),
                preprocessRequest(),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters()
                    .and(
                        parameterWithName("type").description("추천 게시글 종류").optional(),
                        parameterWithName("scope")
                            .description(
                                "게시글 범위 필터. PUBLIC 은 개인 게시글만, WORKSPACE 는 프로젝트 게시글만 조회한다. "
                                    + "생략하면 두 종류를 모두 포함한다.")
                            .optional(),
                        parameterWithName("postType")
                            .description("게시글 형태 필터. SHORT, LONG, QUESTION 중 하나를 사용한다.")
                            .optional()),
                PaginatedGetPostsResponseSnippets.getPostsResponseFields()));
  }

  @Test
  @DisplayName("[getRecommendations] type 파라미터가 주어지면 해당 추천 방식만 사용한다")
  @WithAuthenticatedUser
  void getRecommendations_withType_shouldUseOnlyThatChannel() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    PaginatedGetPostsResponse response =
        PaginatedGetPostsResponseSnippets.getPaginatedGetPostsResponse();

    when(postRecommendationService.getRecommendations(
            eq(new PostRecommendationContext(user.userId(), null, null)),
            eq(PostRecommendationType.TRENDING),
            any()))
        .thenReturn(response);

    mockMvc
        .perform(get("/posts/recommendations").queryParam("type", "TRENDING"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("[getRecommendations] scope 파라미터가 주어지면 해당 범위의 게시글만 조회한다")
  @WithAuthenticatedUser
  void getRecommendations_withScope_shouldFilterByScope() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    PaginatedGetPostsResponse response =
        PaginatedGetPostsResponseSnippets.getPaginatedGetPostsResponse();

    when(postRecommendationService.getRecommendations(
            eq(new PostRecommendationContext(user.userId(), PostScope.WORKSPACE, null)),
            eq(PostRecommendationType.TRENDING),
            any()))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/posts/recommendations")
                .queryParam("type", "TRENDING")
                .queryParam("scope", "WORKSPACE"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("[getRecommendations] postType 파라미터가 주어지면 해당 형태의 게시글만 조회한다")
  @WithAuthenticatedUser
  void getRecommendations_withPostType_shouldFilterByPostType() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    PaginatedGetPostsResponse response =
        PaginatedGetPostsResponseSnippets.getPaginatedGetPostsResponse();

    when(postRecommendationService.getRecommendations(
            eq(new PostRecommendationContext(user.userId(), null, PostType.QUESTION)),
            eq(PostRecommendationType.FOLLOWING),
            any()))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/posts/recommendations")
                .queryParam("type", "FOLLOWING")
                .queryParam("postType", "QUESTION"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("[getRecommendations] 로그인하지 않은 사용자는 접근할 수 없다")
  void getRecommendations_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/posts/recommendations")).andExpect(status().isUnauthorized());
  }
}
