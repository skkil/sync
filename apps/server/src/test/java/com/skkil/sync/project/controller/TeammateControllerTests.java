package com.skkil.sync.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.service.TeammateService;
import com.skkil.sync.project.snippets.AddTeammateRequestSnippets;
import com.skkil.sync.project.snippets.GetProjectTeammatesResponseSnippets;
import com.skkil.sync.project.snippets.UpdateTeammateRequestSnippets;
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

@WebMvcTest(TeammateController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TeammateControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private TeammateService teammateService;

  @Test
  @DisplayName("[getProjectTeammates] API 문서화 테스트")
  void getProjectTeammates() throws Exception {
    String handle = "my-project";
    GetProjectTeammatesResponse response =
        GetProjectTeammatesResponseSnippets.getGetProjectTeammatesResponse();

    when(teammateService.getProjectTeammates(handle)).thenReturn(response);

    mockMvc
        .perform(get("/projects/{handle}/teammates", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectTeammates",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Project Teammates")
                    .description("프로젝트의 팀원 목록을 조회합니다.")
                    .responseSchema(schema(GetProjectTeammatesResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                GetProjectTeammatesResponseSnippets.getGetProjectTeammatesResponseFields()));
  }

  @Test
  @DisplayName("[addTeammate] API 문서화 테스트")
  @WithAuthenticatedUser
  void addTeammate() throws Exception {
    String projectHandle = "my-project";
    AddTeammateRequest request = AddTeammateRequestSnippets.getAddTeammateRequest();

    doNothing().when(teammateService).addTeammate(anyString(), eq(request));

    mockMvc
        .perform(
            post("/projects/{handle}/teammates", projectHandle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "AddTeammate",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Add Teammate")
                    .description("프로젝트에 팀원을 추가합니다.")
                    .requestSchema(schema(AddTeammateRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                AddTeammateRequestSnippets.getAddTeammateRequestFields()));
  }

  @Test
  @DisplayName("[removeTeammate] API 문서화 테스트")
  @WithAuthenticatedUser
  void removeTeammate() throws Exception {
    String projectHandle = "my-project";
    String teammateHandle = "john-doe";

    doNothing().when(teammateService).removeTeammate(projectHandle, teammateHandle);

    mockMvc
        .perform(
            delete("/projects/{handle}/teammates/{teammateHandle}", projectHandle, teammateHandle))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "RemoveTeammate",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Remove Teammate")
                    .description("프로젝트에서 팀원을 제거합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("teammateHandle").description("제거할 팀원의 핸들"))));
  }

  @Test
  @DisplayName("[leaveProject] API 문서화 테스트")
  @WithAuthenticatedUser
  void leaveProject() throws Exception {
    String projectHandle = "my-project";

    doNothing().when(teammateService).leaveProject(anyLong(), eq(projectHandle));

    mockMvc
        .perform(delete("/projects/{handle}/teammates/me", projectHandle))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "LeaveProject",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Leave Project")
                    .description("현재 사용자가 프로젝트에서 나갑니다. 프로젝트 소유자는 나갈 수 없습니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("나갈 프로젝트 핸들"))));
  }

  @Test
  @DisplayName("[updateTeammate] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateTeammate() throws Exception {
    String projectHandle = "my-project";
    String teammateHandle = "john-doe";
    UpdateTeammateRequest request = UpdateTeammateRequestSnippets.getUpdateTeammateRequest();

    doNothing()
        .when(teammateService)
        .updateTeammate(eq(projectHandle), eq(teammateHandle), eq(request));

    mockMvc
        .perform(
            patch("/projects/{handle}/teammates/{teammateHandle}", projectHandle, teammateHandle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateTeammate",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Update Teammate")
                    .description("프로젝트 팀원의 역할을 수정합니다.")
                    .requestSchema(schema(UpdateTeammateRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("teammateHandle").description("수정할 팀원의 핸들")),
                UpdateTeammateRequestSnippets.getUpdateTeammateRequestFields()));
  }
}
