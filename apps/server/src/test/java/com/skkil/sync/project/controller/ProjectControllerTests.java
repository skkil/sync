package com.skkil.sync.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetMyProjectsResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.service.ProjectService;
import com.skkil.sync.project.snippets.CreateProjectRequestSnippets;
import com.skkil.sync.project.snippets.CreateProjectResponseSnippets;
import com.skkil.sync.project.snippets.GetMyProjectsResponseSnippets;
import com.skkil.sync.project.snippets.GetProjectHandleAvailabilityResponseSnippets;
import com.skkil.sync.project.snippets.GetProjectResponseSnippets;
import com.skkil.sync.project.snippets.GetProjectsResponseSnippets;
import com.skkil.sync.project.snippets.UpdateProjectRequestSnippets;
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

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProjectControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private ProjectService projectService;

  @Test
  @DisplayName("[createProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void createProject() throws Exception {
    CreateProjectRequest request = CreateProjectRequestSnippets.getCreateProjectRequest();
    CreateProjectResponse response = CreateProjectResponseSnippets.getCreateProjectResponse();

    when(projectService.createProject(any(Long.class), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Create Project")
                    .description("새로운 프로젝트를 생성합니다.")
                    .requestSchema(schema(CreateProjectRequest.class.getSimpleName()))
                    .responseSchema(schema(CreateProjectResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CreateProjectRequestSnippets.getCreateProjectRequestFields(),
                CreateProjectResponseSnippets.getCreateProjectResponseFields()));
  }

  @Test
  @DisplayName("[getProjectByHandle] API 문서화 테스트")
  void getProjectByHandle() throws Exception {
    GetProjectResponse response = GetProjectResponseSnippets.getGetProjectResponse();

    when(projectService.getProjectByHandle(null, response.summary().handle())).thenReturn(response);

    mockMvc
        .perform(get("/projects/{handle}", response.summary().handle()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectByHandle",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Project By Handle")
                    .description("핸들로 프로젝트를 조회합니다.")
                    .responseSchema(schema(GetProjectResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                GetProjectResponseSnippets.getGetProjectResponseFields()));
  }

  @Test
  @DisplayName("[getProjectHandleAvailability] API 문서화 테스트")
  void getProjectHandleAvailability() throws Exception {
    String handle = "my-project";

    GetProjectHandleAvailabilityResponse response =
        GetProjectHandleAvailabilityResponseSnippets.getGetProjectHandleAvailabilityResponse();

    when(projectService.isProjectHandleAvailable(handle)).thenReturn(response);

    mockMvc
        .perform(get("/projects/handles/availability").queryParam("handle", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectHandleAvailability",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Project Handle Availability")
                    .description("프로젝트 핸들의 사용 가능 여부를 확인합니다.")
                    .responseSchema(
                        schema(GetProjectHandleAvailabilityResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("handle").description("확인할 프로젝트 핸들")),
                GetProjectHandleAvailabilityResponseSnippets.getResponseFields()));
  }

  @Test
  @DisplayName("[getMyProjects] API 문서화 테스트")
  @WithAuthenticatedUser
  void getMyProjects() throws Exception {
    GetMyProjectsResponse response = GetMyProjectsResponseSnippets.getGetMyProjectsResponse();

    when(projectService.getMyProjects(anyLong())).thenReturn(response);

    mockMvc
        .perform(get("/projects/my"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetMyProjects",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get My Projects")
                    .description("현재 사용자가 참여 중인 프로젝트와 실제 운영 정보를 조회합니다.")
                    .responseSchema(schema(GetMyProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetMyProjectsResponseSnippets.getGetMyProjectsResponseFields()));
  }

  @Test
  @DisplayName("[getMyProjects] 로그인하지 않은 사용자는 접근할 수 없다")
  void getMyProjects_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/projects/my")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getProjectsByUser] API 문서화 테스트")
  void getProjectsByUser() throws Exception {
    String handle = "john";
    GetProjectsResponse response = GetProjectsResponseSnippets.getGetProjectsResponse();

    when(projectService.getProjectsByUser(handle)).thenReturn(response);

    mockMvc
        .perform(get("/users/{handle}/projects", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectsByUser",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Projects By User")
                    .description("유저 핸들로 해당 유저가 참여 중인 공개 프로젝트 목록을 조회합니다.")
                    .responseSchema(schema(GetProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("유저 핸들")),
                GetProjectsResponseSnippets.getGetProjectsResponseFields()));
  }

  @Test
  @DisplayName("[searchProjects] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchProjects() throws Exception {
    String query = "Spring";
    GetProjectsResponse response = GetProjectsResponseSnippets.getGetProjectsResponse();

    when(projectService.searchProjects(query)).thenReturn(response);

    mockMvc
        .perform(get("/search/projects").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchProjects",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Search Projects")
                    .description("검색어로 프로젝트를 검색합니다.")
                    .responseSchema(schema(GetProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("query").description("프로젝트 검색어")),
                GetProjectsResponseSnippets.getGetProjectsResponseFields()));
  }

  @Test
  @DisplayName("[searchMyProjects] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchMyProjects() throws Exception {
    String query = "Spring";
    GetProjectsResponse response = GetProjectsResponseSnippets.getGetProjectsResponse();

    when(projectService.searchMyProjects(anyLong(), eq(query))).thenReturn(response);

    mockMvc
        .perform(get("/search/projects/my").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchMyProjects",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Search My Projects")
                    .description("내 프로젝트를 검색어로 검색합니다.")
                    .responseSchema(schema(GetProjectsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("query").description("프로젝트 검색어")),
                GetProjectsResponseSnippets.getGetProjectsResponseFields()));
  }

  @Test
  @DisplayName("[searchProjects] 로그인하지 않은 사용자는 접근할 수 없다")
  void searchProjects_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/search/projects").queryParam("query", "Spring"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[searchMyProjects] 로그인하지 않은 사용자는 접근할 수 없다")
  void searchMyProjects_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/search/projects/my").queryParam("query", "Spring"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[updateProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateProject() throws Exception {
    String projectHandle = "my-project";
    UpdateProjectRequest request = UpdateProjectRequestSnippets.getUpdateProjectRequest();

    doNothing().when(projectService).updateProject(anyLong(), anyString(), eq(request));

    mockMvc
        .perform(
            patch("/projects/{handle}", projectHandle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Update Project")
                    .description("프로젝트 정보를 수정합니다.")
                    .requestSchema(schema(UpdateProjectRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                UpdateProjectRequestSnippets.getUpdateProjectRequestFields()));
  }

  @Test
  @DisplayName("[deleteProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void deleteProject() throws Exception {
    String projectHandle = "my-project";

    doNothing().when(projectService).deleteProject(projectHandle);

    mockMvc
        .perform(delete("/projects/{handle}", projectHandle).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeleteProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Delete Project")
                    .description("프로젝트 소유자가 프로젝트와 프로젝트의 모든 게시글을 삭제합니다."),
                preprocessRequest(RestDocsUtils.removeCsrfFormBody()),
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("삭제할 프로젝트 핸들"))));
  }
}
