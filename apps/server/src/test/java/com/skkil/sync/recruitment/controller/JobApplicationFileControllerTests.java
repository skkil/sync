package com.skkil.sync.recruitment.controller;

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
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.recruitment.dto.request.UploadJobApplicationFileRequest;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationFilesResponse;
import com.skkil.sync.recruitment.service.JobApplicationFileService;
import com.skkil.sync.recruitment.snippets.GetJobApplicationFilesResponseSnippets;
import com.skkil.sync.recruitment.snippets.UploadJobApplicationFileRequestSnippets;
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

@WebMvcTest(JobApplicationFileController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class JobApplicationFileControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private JobApplicationFileService jobApplicationFileService;

  @Test
  @DisplayName("[uploadJobApplicationFile] API 문서화 테스트")
  @WithAuthenticatedUser
  void uploadJobApplicationFile() throws Exception {
    Long applicationId = 1L;
    UploadJobApplicationFileRequest request =
        UploadJobApplicationFileRequestSnippets.getUploadJobApplicationFileRequest();

    mockMvc
        .perform(
            post("/applications/{applicationId}/files", applicationId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UploadJobApplicationFile",
                ResourceSnippetParameters.builder()
                    .tag("applications")
                    .summary("Upload Job Application File")
                    .description("Upload a file to a job application")
                    .requestSchema(schema(UploadJobApplicationFileRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(
                    parameterWithName("applicationId").description("Job Application ID")),
                UploadJobApplicationFileRequestSnippets
                    .getUploadJobApplicationFileRequestFields()));
  }

  @Test
  @DisplayName("[getJobApplicationFiles] API 문서화 테스트")
  @WithAuthenticatedUser
  void getJobApplicationFiles() throws Exception {
    Long applicationId = 1L;
    GetJobApplicationFilesResponse response =
        GetJobApplicationFilesResponseSnippets.getGetJobApplicationFilesResponse();

    when(jobApplicationFileService.getJobApplicationFiles(eq(applicationId))).thenReturn(response);

    mockMvc
        .perform(get("/applications/{applicationId}/files", applicationId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetJobApplicationFiles",
                ResourceSnippetParameters.builder()
                    .tag("applications")
                    .summary("Get Job Application Files")
                    .description("Get all files for a job application")
                    .responseSchema(schema(GetJobApplicationFilesResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("applicationId").description("Job Application ID")),
                GetJobApplicationFilesResponseSnippets.getGetJobApplicationFilesResponseFields()));
  }
}
