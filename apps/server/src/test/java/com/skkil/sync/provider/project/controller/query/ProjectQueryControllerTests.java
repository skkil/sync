package com.skkil.sync.provider.project.controller.query;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.provider.project.dto.response.GetProjectsResponse;
import com.skkil.sync.provider.project.service.query.ProjectQueryService;
import com.skkil.sync.provider.project.snippets.GetProjectsResponseSnippets;
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

@WebMvcTest(ProjectQueryController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProjectQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProjectQueryService projectQueryService;

  @Test
  @DisplayName("[getTrendingProjects] API 문서화 테스트")
  void getTrendingProjects() throws Exception {
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetProjectsResponse response = GetProjectsResponseSnippets.getGetProjectsResponse();

    when(projectQueryService.getTrendingProjects(eq(pagination))).thenReturn(response);

    mockMvc
        .perform(
            get("/projects/trending")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetTrendingProjects",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Trending Projects")
                    .description("Get Trending Projects")
                    .responseSchema(schema(GetProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetProjectsResponseSnippets.getProjectsResponseFields()));
  }
}
