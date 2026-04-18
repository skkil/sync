package com.skkil.sync.recruitment.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.recruitment.dto.request.CreateJobApplicationRequest;
import com.skkil.sync.recruitment.dto.response.CreateJobApplicationResponse;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationResponse;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationsResponse;
import com.skkil.sync.recruitment.service.JobApplicationService;
import com.skkil.sync.recruitment.snippets.CreateJobApplicationRequestSnippets;
import com.skkil.sync.recruitment.snippets.CreateJobApplicationResponseSnippets;
import com.skkil.sync.recruitment.snippets.GetJobApplicationResponseSnippets;
import com.skkil.sync.recruitment.snippets.GetJobApplicationsResponseSnippets;
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

@WebMvcTest(JobApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class JobApplicationControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private JobApplicationService jobApplicationService;

  @Test
  @DisplayName("[createJobApplication] API 문서화 테스트")
  @WithAuthenticatedUser
  void createJobApplication() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateJobApplicationRequest request =
        CreateJobApplicationRequestSnippets.getCreateJobApplicationRequest();
    CreateJobApplicationResponse response =
        CreateJobApplicationResponseSnippets.getCreateJobApplicationResponse();

    when(jobApplicationService.createJobApplication(eq(user.userId()), eq(request)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/applications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateJobApplication",
                ResourceSnippetParameters.builder()
                    .tag("applications")
                    .summary("Create Job Application")
                    .description("Create Job Application")
                    .responseSchema(schema(CreateJobApplicationResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateJobApplicationRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CreateJobApplicationRequestSnippets.getCreateJobApplicationRequestFields(),
                CreateJobApplicationResponseSnippets.getCreateJobApplicationResponseFields()));
  }

  @Test
  @DisplayName("[getMyJobApplications] API 문서화 테스트")
  @WithAuthenticatedUser
  void getMyJobApplications() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    OffsetPaginationRequest pagination = OffsetPaginationRequestSnippets.getPaginationRequest();
    GetJobApplicationsResponse response =
        GetJobApplicationsResponseSnippets.getGetJobApplicationsResponse();

    when(jobApplicationService.getJobApplications(eq(user.userId()), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/applications/me")
                .queryParams(OffsetPaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetMyJobApplications",
                ResourceSnippetParameters.builder()
                    .tag("applications")
                    .summary("Get My Job Applications")
                    .description("Get My Job Applications")
                    .responseSchema(schema(GetJobApplicationsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                OffsetPaginationRequestSnippets.getPaginationRequestParameters(),
                GetJobApplicationsResponseSnippets.getJobApplicationsResponseFields()));
  }

  @Test
  @DisplayName("[getJobApplication] API 문서화 테스트")
  @WithAuthenticatedUser
  void getJobApplication() throws Exception {
    Long applicationId = 1L;
    GetJobApplicationResponse response =
        GetJobApplicationResponseSnippets.getGetJobApplicationResponse();

    when(jobApplicationService.getJobApplication(eq(applicationId))).thenReturn(response);

    mockMvc
        .perform(get("/applications/{applicationId}", applicationId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetJobApplication",
                ResourceSnippetParameters.builder()
                    .tag("applications")
                    .summary("Get Job Application")
                    .description("Get Job Application by ID")
                    .responseSchema(schema(GetJobApplicationResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("applicationId").description("Application ID")),
                GetJobApplicationResponseSnippets.getGetJobApplicationResponseFields()));
  }
}
