package com.skkil.sync.comment.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.service.CommentService;
import com.skkil.sync.comment.snippets.CreateCommentRequestSnippets;
import com.skkil.sync.comment.snippets.CreateCommentResponseSnippets;
import com.skkil.sync.comment.snippets.GetCommentsResponseSnippets;
import com.skkil.sync.comment.snippets.UpdateCommentRequestSnippets;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
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

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
class CommentControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private CommentService commentService;

  @Test
  @DisplayName("[getPostComments] API 문서화 테스트")
  void getPostComments() throws Exception {
    String slug = "test-post";
    GetCommentsResponse response = GetCommentsResponseSnippets.getGetCommentsResponse();

    when(commentService.getPostComments(slug)).thenReturn(response);

    mockMvc
        .perform(get("/posts/{slug}/comments", slug))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostComments",
                ResourceSnippetParameters.builder()
                    .tag("comment")
                    .summary("Get Post Comments")
                    .description("Get Post Comments")
                    .responseSchema(schema(GetCommentsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("slug").description("Post Slug")),
                GetCommentsResponseSnippets.getCommentsResponseFields()));
  }

  @Test
  @DisplayName("[createComment] API 문서화 테스트")
  @WithAuthenticatedUser
  void createComment() throws Exception {
    String slug = "test-post";
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateCommentRequest request = CreateCommentRequestSnippets.getCreateCommentRequest();
    CreateCommentResponse response = CreateCommentResponseSnippets.getCreateCommentResponse();

    when(commentService.createComment(eq(user.userId()), eq(slug), eq(request)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/posts/{slug}/comments", slug)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateComment",
                ResourceSnippetParameters.builder()
                    .tag("comment")
                    .summary("Create Comment")
                    .description("Create Comment")
                    .responseSchema(schema(CreateCommentResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateCommentRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                pathParameters(parameterWithName("slug").description("Post Slug")),
                CreateCommentRequestSnippets.getCreateCommentRequestFields(),
                CreateCommentResponseSnippets.getCreateCommentResponseFields()));
  }

  @Test
  @DisplayName("[updateComment] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateComment() throws Exception {
    UpdateCommentRequest request = UpdateCommentRequestSnippets.getUpdateCommentRequest();

    mockMvc
        .perform(
            patch("/comments/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateComment",
                ResourceSnippetParameters.builder()
                    .tag("comment")
                    .summary("Update Comment")
                    .description("Update Comment")
                    .requestSchema(schema(UpdateCommentRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("commentId").description("Comment ID")),
                UpdateCommentRequestSnippets.getUpdateCommentRequestFields()));
  }

  @Test
  @DisplayName("[deleteComment] API 문서화 테스트")
  @WithAuthenticatedUser
  void deleteComment() throws Exception {
    mockMvc
        .perform(delete("/comments/{commentId}", 1L))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeleteComment",
                ResourceSnippetParameters.builder()
                    .tag("comment")
                    .summary("Delete Comment")
                    .description("Delete Comment"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("commentId").description("Comment ID"))));
  }
}
