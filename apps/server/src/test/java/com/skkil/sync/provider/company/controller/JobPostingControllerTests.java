package com.skkil.sync.provider.company.controller;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.PaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.company.service.JobPostingService;
import com.skkil.sync.provider.company.snippets.CreateJobPostingRequestSnippets;
import com.skkil.sync.provider.company.snippets.CreateJobPostingResponseSnippets;
import com.skkil.sync.provider.company.snippets.GetJobPostingsResponseSnippets;
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

@WebMvcTest(JobPostingController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
public class JobPostingControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private JobPostingService jobPostingService;

  @Autowired private JsonMapper jsonMapper;

  @Test
  @DisplayName("[createJobPosting] API 문서화 테스트")
  void createJobPosting() throws Exception {
    Long companyId = 1L;
    CreateJobPostingRequest request = CreateJobPostingRequestSnippets.getCreateJobPostingRequest();
    CreateJobPostingResponse response =
        CreateJobPostingResponseSnippets.getCreateJobPostingResponse();

    when(jobPostingService.createJobPosting(eq(companyId), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/companies/{companyId}/jobs", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateJobPosting",
                ResourceSnippetParameters.builder()
                    .tag("jobs")
                    .summary("Create Job Posting")
                    .description("Create Job Posting")
                    .responseSchema(schema(CreateJobPostingResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateJobPostingRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("companyId").description("Company ID")),
                CreateJobPostingRequestSnippets.getCreateJobPostingRequestFields(),
                CreateJobPostingResponseSnippets.getCreateJobPostingResponseFields()));
  }

  @Test
  @DisplayName("[getJobPostings] API 문서화 테스트")
  void getJobPostings() throws Exception {
    PaginationRequest pagination = PaginationRequestSnippets.getPaginationRequest();
    GetJobPostingsResponse response = GetJobPostingsResponseSnippets.getGetJobPostingsResponse();

    when(jobPostingService.getJobPostings(eq(pagination))).thenReturn(response);

    mockMvc
        .perform(
            get("/jobs").queryParams(PaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetJobPostings",
                ResourceSnippetParameters.builder()
                    .tag("jobs")
                    .summary("Get Job Postings")
                    .description("Get Job Postings")
                    .responseSchema(schema(GetJobPostingsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                PaginationRequestSnippets.getPaginationRequestParameters(),
                GetJobPostingsResponseSnippets.getJobPostingsResponseFields()));
  }

  @Test
  @DisplayName("[getJobPostingsByCompany] API 문서화 테스트")
  void getJobPostingsByCompany() throws Exception {
    Long companyId = 1L;
    PaginationRequest pagination = PaginationRequestSnippets.getPaginationRequest();
    GetJobPostingsResponse response = GetJobPostingsResponseSnippets.getGetJobPostingsResponse();

    when(jobPostingService.getJobPostingsByCompany(eq(companyId), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/companies/{companyId}/jobs", companyId)
                .queryParams(PaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetJobPostingsByCompany",
                ResourceSnippetParameters.builder()
                    .tag("jobs")
                    .summary("Get Job Postings By Company")
                    .description("Get Job Postings By Company")
                    .responseSchema(schema(GetJobPostingsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("companyId").description("Company ID")),
                PaginationRequestSnippets.getPaginationRequestParameters(),
                GetJobPostingsResponseSnippets.getJobPostingsResponseFields()));
  }
}
