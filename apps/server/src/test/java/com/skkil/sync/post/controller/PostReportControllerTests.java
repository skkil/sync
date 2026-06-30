package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.ReportPostRequest;
import com.skkil.sync.post.service.PostReportService;
import com.skkil.sync.post.snippets.ReportPostRequestSnippets;
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

@WebMvcTest(PostReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostReportControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private PostReportService postReportService;

  @Test
  @DisplayName("[reportPost] API 문서화 테스트")
  @WithAuthenticatedUser
  void reportPost() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long postId = 1L;
    ReportPostRequest request = ReportPostRequestSnippets.getReportPostRequest();

    doNothing().when(postReportService).reportPost(eq(user.userId()), eq(postId), eq(request));

    mockMvc
        .perform(
            post("/posts/{postId}/reports", postId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "ReportPost",
                ResourceSnippetParameters.builder()
                    .tag("post-report")
                    .summary("Report Post")
                    .description("부적절한 포스트를 신고합니다.")
                    .requestSchema(schema(ReportPostRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("postId").description("포스트 ID")),
                ReportPostRequestSnippets.getReportPostRequestFields()));
  }
}
