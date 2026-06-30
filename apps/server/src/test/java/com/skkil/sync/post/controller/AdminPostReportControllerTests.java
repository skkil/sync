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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.ReviewPostReportRequest;
import com.skkil.sync.post.dto.response.GetPostReportsResponse;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.service.PostReportService;
import com.skkil.sync.post.snippets.GetPostReportsResponseSnippets;
import com.skkil.sync.post.snippets.ReviewPostReportRequestSnippets;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AdminPostReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class AdminPostReportControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private PostReportService postReportService;

  @Test
  @DisplayName("[getPostReports] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void getPostReports() throws Exception {
    OffsetPaginationRequest pagination = OffsetPaginationRequestSnippets.getPaginationRequest();
    GetPostReportsResponse response = GetPostReportsResponseSnippets.getPostReportsResponse();

    when(postReportService.getReports(PostReportStatus.PENDING, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/admin/post-reports")
                .queryParam("status", PostReportStatus.PENDING.name())
                .queryParams(OffsetPaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostReports",
                ResourceSnippetParameters.builder()
                    .tag("post-report")
                    .summary("Get Post Reports")
                    .description("관리자 신고 큐를 조회합니다.")
                    .responseSchema(schema(GetPostReportsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("status").description("신고 상태").optional(),
                    parameterWithName("page").description("Page (Integer)").optional(),
                    parameterWithName("size").description("Page Size (Integer)")),
                GetPostReportsResponseSnippets.getPostReportsResponseFields()));
  }

  @Test
  @DisplayName("[reviewPostReport] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void reviewPostReport() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long reportId = 1L;
    ReviewPostReportRequest request = ReviewPostReportRequestSnippets.getReviewPostReportRequest();

    doNothing().when(postReportService).reviewReport(eq(user.userId()), eq(reportId), eq(request));

    mockMvc
        .perform(
            patch("/admin/post-reports/{reportId}", reportId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "ReviewPostReport",
                ResourceSnippetParameters.builder()
                    .tag("post-report")
                    .summary("Review Post Report")
                    .description("관리자가 신고를 기각하거나 포스트 숨김으로 처리합니다.")
                    .requestSchema(schema(ReviewPostReportRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("reportId").description("신고 ID")),
                ReviewPostReportRequestSnippets.getReviewPostReportRequestFields()));
  }
}
