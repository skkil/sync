package com.skkil.sync.provider.contest.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.PaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.provider.contest.dto.request.CreateContestOccurrenceRequest;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrenceResponse;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrencesResponse;
import com.skkil.sync.provider.contest.service.ContestOccurrenceService;
import com.skkil.sync.provider.contest.snippets.CreateContestOccurrenceRequestSnippets;
import com.skkil.sync.provider.contest.snippets.GetContestOccurrenceResponseSnippets;
import com.skkil.sync.provider.contest.snippets.GetContestOccurrencesResponseSnippets;
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

@WebMvcTest(ContestOccurrenceController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ContestOccurrenceControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private ContestOccurrenceService contestOccurrenceService;

  @Test
  @DisplayName("[createContestOccurrence] API 문서화 테스트")
  @WithAuthenticatedUser
  void createContestOccurrence() throws Exception {
    Long contestId = 1L;
    CreateContestOccurrenceRequest request =
        CreateContestOccurrenceRequestSnippets.getCreateContestOccurrenceRequest();

    doNothing().when(contestOccurrenceService).createContestOccurrence(eq(contestId), eq(request));

    mockMvc
        .perform(
            post("/contests/{contestId}/occurrences", contestId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateContestOccurrence",
                ResourceSnippetParameters.builder()
                    .tag("contests")
                    .summary("Create Contest Occurrence")
                    .description("Create Contest Occurrence")
                    .requestSchema(schema(CreateContestOccurrenceRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("contestId").description("Contest ID")),
                CreateContestOccurrenceRequestSnippets.getCreateContestOccurrenceRequestFields()));
  }

  @Test
  @DisplayName("[getContestOccurrencesByContest] API 문서화 테스트")
  @WithAuthenticatedUser
  void getContestOccurrencesByContest() throws Exception {
    Long contestId = 1L;
    PaginationRequest pagination = PaginationRequestSnippets.getPaginationRequest();
    GetContestOccurrencesResponse response =
        GetContestOccurrencesResponseSnippets.getGetContestOccurrencesResponse();

    when(contestOccurrenceService.getContestOccurrencesByContest(eq(contestId), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/contests/{contestId}/occurrences", contestId)
                .queryParams(PaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetContestOccurrencesByContest",
                ResourceSnippetParameters.builder()
                    .tag("contests")
                    .summary("Get Contest Occurrences by Contest")
                    .description("Get Contest Occurrences by Contest")
                    .responseSchema(schema(GetContestOccurrencesResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("contestId").description("Contest ID")),
                PaginationRequestSnippets.getPaginationRequestParameters(),
                GetContestOccurrencesResponseSnippets.getGetContestOccurrencesResponseFields()));
  }

  @Test
  @DisplayName("[getContestOccurrence] API 문서화 테스트")
  @WithAuthenticatedUser
  void getContestOccurrence() throws Exception {
    Long contestId = 1L;
    Long occurrenceId = 1L;
    GetContestOccurrenceResponse response =
        GetContestOccurrenceResponseSnippets.getGetContestOccurrenceResponse();

    when(contestOccurrenceService.getContestOccurrence(eq(contestId), eq(occurrenceId)))
        .thenReturn(response);

    mockMvc
        .perform(get("/contests/{contestId}/occurrences/{occurrenceId}", contestId, occurrenceId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetContestOccurrence",
                ResourceSnippetParameters.builder()
                    .tag("contests")
                    .summary("Get Contest Occurrence")
                    .description("Get Contest Occurrence")
                    .responseSchema(schema(GetContestOccurrenceResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("contestId").description("Contest ID"),
                    parameterWithName("occurrenceId").description("Contest Occurrence ID")),
                GetContestOccurrenceResponseSnippets.getGetContestOccurrenceResponseFields()));
  }
}
