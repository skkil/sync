package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.service.PostQueryService;
import com.skkil.sync.post.snippets.GetPostResponseSnippets;
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

@WebMvcTest(PostQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostQueryService postQueryService;

  @Test
  @DisplayName("[getPosts] API 문서화 테스트")
  void getPosts() throws Exception {
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetPostsResponse response = GetPostsResponseSnippets.getGetPostsResponse();

    when(postQueryService.getPosts(pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/posts")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPosts",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Posts")
                    .description("Get Posts")
                    .responseSchema(schema("GetPostsResponse")),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetPostsResponseSnippets.getPostsResponseFields()));
  }

  @Test
  @DisplayName("[getPostBySlug] API 문서화 테스트")
  @WithAuthenticatedUser
  void getPostBySlug() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String slug = "test-slug";
    GetPostResponse response = GetPostResponseSnippets.getGetPostResponse();

    when(postQueryService.getPostBySlug(eq(user.userId()), eq(slug))).thenReturn(response);

    mockMvc
        .perform(get("/posts/{slug}", slug))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostBySlug",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Post By Slug")
                    .description("Get Post By Slug")
                    .responseSchema(schema(GetPostResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetPostResponseSnippets.getPostResponseFields()));
  }

  @Test
  @DisplayName("[getUserPosts] API 문서화 테스트")
  void getUserPosts() throws Exception {
    Long userId = 1L;
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetPostsResponse response = GetPostsResponseSnippets.getGetPostsResponse();

    when(postQueryService.getUserPosts(userId, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/users/{userId}/posts", userId)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetUserPosts",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Posts")
                    .description("Get Posts")
                    .responseSchema(schema("GetPostsResponse")),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("User ID")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetPostsResponseSnippets.getPostsResponseFields()));
  }

  @Test
  @DisplayName("[getPostsByProject] API 문서화 테스트")
  void getPostsByProject() throws Exception {
    String handle = "project";

    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetPostsResponse response = GetPostsResponseSnippets.getGetPostsResponse();

    when(postQueryService.getPostsByProject(handle, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/projects/{handle}/posts", handle)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostsByProject",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Posts By Project")
                    .description("Get Posts By Project")
                    .responseSchema(schema(GetPostsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetPostsResponseSnippets.getPostsResponseFields()));
  }
}
