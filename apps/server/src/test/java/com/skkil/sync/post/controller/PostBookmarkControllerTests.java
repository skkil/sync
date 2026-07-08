package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.service.PostBookmarkService;
import com.skkil.sync.post.snippets.GetPostsResponseSnippets;
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

@WebMvcTest(PostBookmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostBookmarkControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostBookmarkService postBookmarkService;

  @Test
  @DisplayName("[bookmarkPost] API 문서화 테스트")
  @WithAuthenticatedUser
  void bookmarkPost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long postId = 2L;

    doNothing().when(postBookmarkService).bookmarkPost(eq(user.userId()), eq(postId));

    mockMvc
        .perform(post("/posts/{postId}/bookmarks", postId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "BookmarkPost",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Bookmark Post")
                    .description("Bookmark Post"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID"))));
  }

  @Test
  @DisplayName("[unbookmarkPost] API 문서화 테스트")
  @WithAuthenticatedUser
  void unbookmarkPost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long postId = 2L;

    doNothing().when(postBookmarkService).unbookmarkPost(eq(user.userId()), eq(postId));

    mockMvc
        .perform(delete("/posts/{postId}/bookmarks", postId))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnbookmarkPost",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Unbookmark Post")
                    .description("Unbookmark Post"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID"))));
  }

  @Test
  @DisplayName("[getBookmarkedPosts] API 문서화 테스트")
  @WithAuthenticatedUser
  void getBookmarkedPosts() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetPostsResponse response = GetPostsResponseSnippets.getGetBookmarkedPostsResponse();

    when(postBookmarkService.getBookmarkedPosts(eq(user.userId()), isNull(), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/bookmarks/posts")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetBookmarkedPosts",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Get Bookmarked Posts")
                    .description("Get Bookmarked Posts")
                    .responseSchema(schema(GetPostsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters()
                    .and(
                        parameterWithName("projectHandle")
                            .description("프로젝트로 검색 범위 제한 (선택)")
                            .optional()),
                GetPostsResponseSnippets.getBookmarkedPostsResponseFields()));
  }
}
