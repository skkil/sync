package com.skkil.sync.provider.project.controller;

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
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import com.skkil.sync.provider.project.service.TeamBuildingService;
import com.skkil.sync.provider.project.snippets.GetTeamBuildingPostsResponseSnippets;
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

@WebMvcTest(TeamBuildingController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TeamBuildingControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TeamBuildingService teamBuildingService;

  @Test
  @DisplayName("[getTeamBuildingPostsByProject] API 문서화 테스트")
  void getTeamBuildingPostsByProject() throws Exception {
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetTeamBuildingPostsResponse response =
        GetTeamBuildingPostsResponseSnippets.getGetTeamBuildingPostsResponse();

    when(teamBuildingService.getTeamBuildingPostsByProject(eq(1L), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/projects/{projectId}/team-building", 1L)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetTeamBuildingPostsByProject",
                ResourceSnippetParameters.builder()
                    .tag("team-building")
                    .summary("Get Team Building Posts By Project")
                    .description("Get Team Building Posts By Project")
                    .responseSchema(schema(GetTeamBuildingPostsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetTeamBuildingPostsResponseSnippets.getTeamBuildingPostsResponseFields()));
  }

  @Test
  @DisplayName("[getTeamBuildingPosts] API 문서화 테스트")
  void getTeamBuildingPosts() throws Exception {
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetTeamBuildingPostsResponse response =
        GetTeamBuildingPostsResponseSnippets.getGetTeamBuildingPostsResponse();

    when(teamBuildingService.getTeamBuildingPosts(eq(pagination))).thenReturn(response);

    mockMvc
        .perform(
            get("/team-building")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetTeamBuildingPosts",
                ResourceSnippetParameters.builder()
                    .tag("team-building")
                    .summary("Get Team Building Posts")
                    .description("Get Team Building Posts")
                    .responseSchema(schema(GetTeamBuildingPostsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetTeamBuildingPostsResponseSnippets.getTeamBuildingPostsResponseFields()));
  }
}
