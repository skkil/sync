package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.dto.response.CreatePostResponse;
import com.skkil.sync.post.service.PostService;
import com.skkil.sync.post.snippets.CreatePostRequestSnippets;
import com.skkil.sync.post.snippets.CreatePostResponseSnippets;
import com.skkil.sync.post.snippets.UpdatePostRequestSnippets;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private PostService postService;

  @Test
  @DisplayName("[createPost] API 문서화 테스트")
  @WithAuthenticatedUser
  void createPost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreatePostRequest request = CreatePostRequestSnippets.getCreatePostRequest();
    CreatePostResponse response = CreatePostResponseSnippets.getCreatePostResponse();

    when(postService.createPost(eq(user.userId()), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreatePost",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Create Post")
                    .description("Create Post")
                    .responseSchema(schema(CreatePostResponse.class.getSimpleName()))
                    .requestSchema(schema(CreatePostRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                CreatePostRequestSnippets.getCreatePostRequestFields(),
                CreatePostResponseSnippets.getCreatePostResponseFields()));
  }

  @Test
  @DisplayName("[updatePost] API 문서화 테스트")
  @WithAuthenticatedUser
  void updatePost() throws Exception {
    Long postId = 1L;
    UpdatePostRequest request = UpdatePostRequestSnippets.getUpdatePostRequest();

    doNothing().when(postService).updatePost(eq(postId), eq(request));

    mockMvc
        .perform(
            patch("/posts/{postId}", postId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdatePost",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Update Post")
                    .description("Update Post")
                    .requestSchema(schema(UpdatePostRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID")),
                UpdatePostRequestSnippets.getUpdatePostRequestFields()));
  }

  @Test
  @DisplayName("[likePost] API 문서화 테스트")
  @WithAuthenticatedUser
  void likePost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    doNothing().when(postService).likePost(eq(user.userId()), eq(1L));

    mockMvc
        .perform(put("/posts/{postId}/likes", 1L).with(csrf().asHeader()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "LikePost",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Like Post")
                    .description("Like Post"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID"))));
  }

  @Test
  @DisplayName("[unlikePost] API 문서화 테스트")
  @WithAuthenticatedUser
  void unlikePost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    doNothing().when(postService).unlikePost(eq(user.userId()), eq(1L));

    mockMvc
        .perform(delete("/posts/{postId}/likes", 1L).with(csrf().asHeader()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnlikePost",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Unlike Post")
                    .description("Unlike Post"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID"))));
  }

  @Test
  @DisplayName("[deletePost] API 문서화 테스트")
  @WithAuthenticatedUser
  void deletePost() throws Exception {
    doNothing().when(postService).deletePost(eq(1L));

    mockMvc
        .perform(delete("/posts/{postId}", 1L))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeletePost",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Delete Post")
                    .description("Delete Post"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("postId").description("Post ID"))));
  }
}
