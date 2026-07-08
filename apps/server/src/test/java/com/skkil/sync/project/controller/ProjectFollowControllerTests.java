package com.skkil.sync.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.project.dto.response.GetProjectFollowersResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.service.ProjectFollowService;
import com.skkil.sync.project.snippets.GetProjectFollowersResponseSnippets;
import com.skkil.sync.project.snippets.GetProjectsResponseSnippets;
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

@WebMvcTest(ProjectFollowController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProjectFollowControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProjectFollowService projectFollowService;

  @Test
  @DisplayName("[followProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void followProject() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String handle = "project-handle";

    doNothing().when(projectFollowService).followProject(eq(follower.userId()), eq(handle));

    mockMvc
        .perform(
            post("/projects/{handle}/follow", handle)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "FollowProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Follow Project")
                    .description("프로젝트를 팔로우합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("팔로우할 프로젝트 Handle"))));
  }

  @Test
  @DisplayName("[unfollowProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void unfollowProject() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String handle = "project-handle";

    doNothing().when(projectFollowService).unfollowProject(eq(follower.userId()), eq(handle));

    mockMvc
        .perform(
            delete("/projects/{handle}/unfollow", handle)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnfollowProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Unfollow Project")
                    .description("프로젝트 팔로우를 취소합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("언팔로우할 프로젝트 Handle"))));
  }

  @Test
  @DisplayName("[getProjectFollowers] API 문서화 테스트")
  void getProjectFollowers() throws Exception {
    String handle = "project-handle";
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetProjectFollowersResponse response =
        GetProjectFollowersResponseSnippets.getGetProjectFollowersResponse();

    when(projectFollowService.getFollowers(eq(null), eq(handle), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/projects/{handle}/followers", handle)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectFollowers",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Project Followers")
                    .description("프로젝트를 팔로우하는 사용자 목록을 조회합니다.")
                    .responseSchema(schema(GetProjectFollowersResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 Handle")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetProjectFollowersResponseSnippets.getProjectFollowersResponseFields()));
  }

  @Test
  @DisplayName("[getFollowedProjects] API 문서화 테스트")
  void getFollowedProjects() throws Exception {
    String handle = "john";
    GetProjectsResponse response = GetProjectsResponseSnippets.getGetProjectsResponse();

    when(projectFollowService.getFollowedProjects(handle)).thenReturn(response);

    mockMvc
        .perform(get("/users/{handle}/followed-projects", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetFollowedProjects",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Followed Projects")
                    .description("유저가 팔로우하는 프로젝트 목록을 조회합니다.")
                    .responseSchema(schema(GetProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("유저 핸들")),
                GetProjectsResponseSnippets.getGetProjectsResponseFields()));
  }
}
