package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.service.TagService;
import com.skkil.sync.post.snippets.CreateTagRequestSnippets;
import com.skkil.sync.post.snippets.CreateTagResponseSnippets;
import com.skkil.sync.post.snippets.GetTagsResponseSnippets;
import com.skkil.sync.user.constant.Role;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TagControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private TagService tagService;

  @Test
  @DisplayName("[searchTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void searchTags() throws Exception {
    String query = "java";
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.searchTags(null, query)).thenReturn(response);

    mockMvc
        .perform(get("/search/tags").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Search Tags")
                    .description("검색어로 태그를 검색합니다.")
                    .responseSchema(schema(GetTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("handle")
                        .description("프로젝트 핸들 (지정 시 프로젝트 범위로 검색)")
                        .optional(),
                    parameterWithName("query").description("태그 검색어")),
                GetTagsResponseSnippets.getGetTagsResponseFields()));
  }

  @Test
  @DisplayName("[searchTags] handle 파라미터로 프로젝트 범위 태그를 검색할 수 있다")
  @WithAuthenticatedUser
  void searchTags_withHandle_searchesProjectTags() throws Exception {
    String handle = "my-project";
    String query = "java";
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.searchTags(handle, query)).thenReturn(response);

    mockMvc
        .perform(get("/search/tags").queryParam("handle", handle).queryParam("query", query))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("[searchTags] 로그인하지 않은 사용자는 접근할 수 없다")
  void searchTags_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/search/tags").queryParam("query", "java"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getProjectTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectTags() throws Exception {
    String handle = "my-project";
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.getProjectTags(handle)).thenReturn(response);

    mockMvc
        .perform(get("/projects/{handle}/tags", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get Project Tags")
                    .description("프로젝트의 태그 목록을 조회합니다.")
                    .responseSchema(schema(GetTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                GetTagsResponseSnippets.getGetTagsResponseFields()));
  }

  @Test
  @DisplayName("[getUnverifiedTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void getUnverifiedTags() throws Exception {
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.getUnverifiedTags()).thenReturn(response);

    mockMvc
        .perform(get("/tags/unverified"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetUnverifiedTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get Unverified Tags")
                    .description("인증되지 않은 전역 태그 목록을 조회합니다. 관리자만 접근할 수 있습니다.")
                    .responseSchema(schema(GetTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetTagsResponseSnippets.getGetTagsResponseFields()));
  }

  @Test
  @DisplayName("[getUnverifiedTags] 로그인하지 않은 사용자는 접근할 수 없다")
  void getUnverifiedTags_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/tags/unverified")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getProjectUnverifiedTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectUnverifiedTags() throws Exception {
    String handle = "my-project";
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.getProjectUnverifiedTags(handle)).thenReturn(response);

    mockMvc
        .perform(get("/projects/{handle}/tags/unverified", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectUnverifiedTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get Project Unverified Tags")
                    .description("프로젝트의 인증되지 않은 태그 목록을 조회합니다. 프로젝트 관리자만 접근할 수 있습니다.")
                    .responseSchema(schema(GetTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                GetTagsResponseSnippets.getGetTagsResponseFields()));
  }

  @Test
  @DisplayName("[verifyTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void verifyTag() throws Exception {
    Long tagId = 1L;

    doNothing().when(tagService).verifyTag(tagId);

    mockMvc
        .perform(patch("/tags/{tagId}/verify", tagId).with(csrf().asHeader()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "VerifyTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Verify Tag")
                    .description("태그를 인증합니다. 전역 태그는 관리자만, 프로젝트 태그는 프로젝트 관리자만 인증할 수 있습니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("tagId").description("인증할 태그 ID"))));
  }

  @Test
  @DisplayName("[verifyTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void verifyTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(patch("/tags/{tagId}/verify", 1L).with(csrf().asHeader()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[createTag] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void createTag() throws Exception {
    CreateTagRequest request = CreateTagRequestSnippets.getCreateTagRequest();
    CreateTagResponse response = CreateTagResponseSnippets.getCreateTagResponse();

    when(tagService.createTag(eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Create Tag")
                    .description("전역 태그를 생성합니다. 관리자만 접근할 수 있습니다.")
                    .requestSchema(schema(CreateTagRequest.class.getSimpleName()))
                    .responseSchema(schema(CreateTagResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CreateTagRequestSnippets.getCreateTagRequestFields(),
                CreateTagResponseSnippets.getCreateTagResponseFields()));
  }

  @Test
  @DisplayName("[createTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void createTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    CreateTagRequest request = CreateTagRequestSnippets.getCreateTagRequest();

    mockMvc
        .perform(
            post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[createProjectTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void createProjectTag() throws Exception {
    String handle = "my-project";
    CreateTagRequest request = CreateTagRequestSnippets.getCreateTagRequest();
    CreateTagResponse response = CreateTagResponseSnippets.getCreateTagResponse();

    when(tagService.createProjectTag(eq(handle), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/projects/{handle}/tags", handle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateProjectTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Create Project Tag")
                    .description("프로젝트 태그를 생성합니다. 프로젝트 관리자만 접근할 수 있습니다.")
                    .requestSchema(schema(CreateTagRequest.class.getSimpleName()))
                    .responseSchema(schema(CreateTagResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("프로젝트 핸들")),
                CreateTagRequestSnippets.getCreateTagRequestFields(),
                CreateTagResponseSnippets.getCreateTagResponseFields()));
  }

  @Test
  @DisplayName("[createProjectTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void createProjectTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    String handle = "my-project";
    CreateTagRequest request = CreateTagRequestSnippets.getCreateTagRequest();

    mockMvc
        .perform(
            post("/projects/{handle}/tags", handle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}
