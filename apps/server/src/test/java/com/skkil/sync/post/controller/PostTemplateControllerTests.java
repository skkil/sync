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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.CreatePostTemplateRequest;
import com.skkil.sync.post.dto.request.UpdatePostTemplateRequest;
import com.skkil.sync.post.dto.response.CreatePostTemplateResponse;
import com.skkil.sync.post.dto.response.GetPostTemplatesResponse;
import com.skkil.sync.post.service.PostTemplateService;
import com.skkil.sync.post.snippets.CreatePostTemplateRequestSnippets;
import com.skkil.sync.post.snippets.CreatePostTemplateResponseSnippets;
import com.skkil.sync.post.snippets.GetPostTemplatesResponseSnippets;
import com.skkil.sync.post.snippets.UpdatePostTemplateRequestSnippets;
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

@WebMvcTest(PostTemplateController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostTemplateControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private PostTemplateService templateService;

  @Test
  @DisplayName("[getProjectTemplates] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectTemplates() throws Exception {
    String handle = "project-handle";
    GetPostTemplatesResponse response =
        GetPostTemplatesResponseSnippets.getGetPostTemplatesResponse();

    when(templateService.getProjectTemplates(eq(handle))).thenReturn(response);

    mockMvc
        .perform(get("/projects/{handle}/post-templates", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectPostTemplates",
                ResourceSnippetParameters.builder()
                    .tag("post-template")
                    .summary("Get Project Post Templates")
                    .description("프로젝트의 글 템플릿 목록을 조회합니다. 글을 쓸 수 있는 팀원만 조회할 수 있습니다.")
                    .responseSchema(schema(GetPostTemplatesResponse.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                GetPostTemplatesResponseSnippets.getGetPostTemplatesResponseFields()));
  }

  @Test
  @DisplayName("[createTemplate] API 문서화 테스트")
  @WithAuthenticatedUser
  void createTemplate() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String handle = "project-handle";
    CreatePostTemplateRequest request =
        CreatePostTemplateRequestSnippets.getCreatePostTemplateRequest();
    CreatePostTemplateResponse response =
        CreatePostTemplateResponseSnippets.getCreatePostTemplateResponse();

    when(templateService.createTemplate(eq(user.userId()), eq(handle), eq(request)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/projects/{handle}/post-templates", handle)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateProjectPostTemplate",
                ResourceSnippetParameters.builder()
                    .tag("post-template")
                    .summary("Create Project Post Template")
                    .description("프로젝트에 글 템플릿을 생성합니다. 프로젝트 관리자만 생성할 수 있습니다.")
                    .responseSchema(schema(CreatePostTemplateResponse.class.getSimpleName()))
                    .requestSchema(schema(CreatePostTemplateRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                CreatePostTemplateRequestSnippets.getCreatePostTemplateRequestFields(),
                CreatePostTemplateResponseSnippets.getCreatePostTemplateResponseFields()));
  }

  @Test
  @DisplayName("[updateTemplate] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateTemplate() throws Exception {
    String handle = "project-handle";
    String externalId = "template-external-id";
    UpdatePostTemplateRequest request =
        UpdatePostTemplateRequestSnippets.getUpdatePostTemplateRequest();

    doNothing().when(templateService).updateTemplate(eq(handle), eq(externalId), eq(request));

    mockMvc
        .perform(
            patch("/projects/{handle}/post-templates/{externalId}", handle, externalId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateProjectPostTemplate",
                ResourceSnippetParameters.builder()
                    .tag("post-template")
                    .summary("Update Project Post Template")
                    .description("프로젝트 글 템플릿을 수정합니다. 모든 필드를 전체 교체하며, 프로젝트 관리자만 수정할 수 있습니다.")
                    .requestSchema(schema(UpdatePostTemplateRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("externalId").description("템플릿 외부 식별자")),
                UpdatePostTemplateRequestSnippets.getUpdatePostTemplateRequestFields()));
  }

  @Test
  @DisplayName("[deleteTemplate] API 문서화 테스트")
  @WithAuthenticatedUser
  void deleteTemplate() throws Exception {
    String handle = "project-handle";
    String externalId = "template-external-id";

    doNothing().when(templateService).deleteTemplate(eq(handle), eq(externalId));

    mockMvc
        .perform(delete("/projects/{handle}/post-templates/{externalId}", handle, externalId))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeleteProjectPostTemplate",
                ResourceSnippetParameters.builder()
                    .tag("post-template")
                    .summary("Delete Project Post Template")
                    .description("프로젝트 글 템플릿을 삭제합니다. 프로젝트 관리자만 삭제할 수 있습니다."),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("externalId").description("템플릿 외부 식별자"))));
  }
}
