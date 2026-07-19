package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationRequestSnippets;
import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.request.MergeTagsRequest;
import com.skkil.sync.post.dto.request.UpdateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetAllTagsResponse;
import com.skkil.sync.post.dto.response.GetTagResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.service.TagService;
import com.skkil.sync.post.snippets.CreateTagRequestSnippets;
import com.skkil.sync.post.snippets.CreateTagResponseSnippets;
import com.skkil.sync.post.snippets.GetAllTagsResponseSnippets;
import com.skkil.sync.post.snippets.GetTagResponseSnippets;
import com.skkil.sync.post.snippets.GetTagsResponseSnippets;
import com.skkil.sync.post.snippets.UpdateTagRequestSnippets;
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

    when(tagService.searchTags(1L, null, query)).thenReturn(response);

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

    when(tagService.searchTags(1L, handle, query)).thenReturn(response);

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
  @DisplayName("[getTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void getTag() throws Exception {
    Long id = 1L;
    GetTagResponse response = GetTagResponseSnippets.getGetTagResponse();

    when(tagService.getTag(1L, id)).thenReturn(response.tag());

    mockMvc
        .perform(get("/tags/{id}", id))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get Tag")
                    .description(
                        "태그 ID로 태그 상세 정보를 조회합니다. 프로젝트 태그는 프로젝트가 공개이거나 요청자가 프로젝트 팀원인 경우에만 "
                            + "조회할 수 있습니다.")
                    .responseSchema(schema(GetTagResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("id").description("태그 ID")),
                GetTagResponseSnippets.getGetTagResponseFields()));
  }

  @Test
  @DisplayName("[getTag] 로그인하지 않은 사용자도 공개 태그를 조회할 수 있다")
  void getTag_unauthenticatedUser_shouldReturnOk() throws Exception {
    Long id = 1L;
    GetTagResponse response = GetTagResponseSnippets.getGetTagResponse();

    when(tagService.getTag(isNull(), eq(id))).thenReturn(response.tag());

    mockMvc.perform(get("/tags/{id}", id)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("[getProjectTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectTags() throws Exception {
    String handle = "my-project";
    var response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.getProjectTags(1L, handle)).thenReturn(response);

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

    when(tagService.getUnverifiedTags(1L)).thenReturn(response);

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

    when(tagService.getProjectUnverifiedTags(1L, handle)).thenReturn(response);

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
  @WithAuthenticatedUser(role = Role.ADMIN)
  void verifyTag() throws Exception {
    String name = "java";

    doNothing().when(tagService).verifyTag(name);

    mockMvc
        .perform(patch("/tags/{name}/verify", name).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "VerifyTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Verify Tag")
                    .description("전역 태그를 인증합니다. 관리자만 접근할 수 있습니다."),
                preprocessRequest(RestDocsUtils.removeCsrfFormBody()),
                null,
                Function.identity(),
                pathParameters(parameterWithName("name").description("인증할 태그 이름"))));
  }

  @Test
  @DisplayName("[verifyTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void verifyTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(patch("/tags/{name}/verify", "java").with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[verifyProjectTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void verifyProjectTag() throws Exception {
    String handle = "my-project";
    String name = "java";

    doNothing().when(tagService).verifyProjectTag(handle, name);

    mockMvc
        .perform(patch("/projects/{handle}/tags/{name}/verify", handle, name).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "VerifyProjectTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Verify Project Tag")
                    .description("프로젝트 태그를 인증합니다. 프로젝트 관리자만 접근할 수 있습니다."),
                preprocessRequest(RestDocsUtils.removeCsrfFormBody()),
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("name").description("인증할 태그 이름"))));
  }

  @Test
  @DisplayName("[verifyProjectTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void verifyProjectTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(patch("/projects/{handle}/tags/{name}/verify", "my-project", "java").with(csrf()))
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

  @Test
  @DisplayName("[getAllTags] API 문서화 테스트")
  @WithAuthenticatedUser
  void getAllTags() throws Exception {
    GetAllTagsResponse response = GetAllTagsResponseSnippets.getGetAllTagsResponse();

    when(tagService.getAllTags(eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(response);

    mockMvc
        .perform(
            get("/tags")
                .queryParams(OffsetPaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetAllTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get All Tags")
                    .description("인증된 전역 태그 전체 목록을 페이지 단위로 조회합니다.")
                    .responseSchema(schema(GetAllTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                OffsetPaginationRequestSnippets.getPaginationRequestParameters(),
                GetAllTagsResponseSnippets.getGetAllTagsResponseFields()));
  }

  @Test
  @DisplayName("[getAllTags] 로그인하지 않은 사용자는 접근할 수 없다")
  void getAllTags_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/tags")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[updateTag] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void updateTag() throws Exception {
    String name = "java";
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    doNothing().when(tagService).updateTag(eq(name), eq(request));

    mockMvc
        .perform(
            patch("/tags/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Update Tag")
                    .description("전역 태그 정보를 수정합니다. 관리자만 접근할 수 있습니다.")
                    .requestSchema(schema(UpdateTagRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("name").description("수정할 태그의 현재 이름")),
                UpdateTagRequestSnippets.getUpdateTagRequestFields()));
  }

  @Test
  @DisplayName("[updateTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void updateTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    mockMvc
        .perform(
            patch("/tags/{name}", "java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[updateProjectTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateProjectTag() throws Exception {
    String handle = "my-project";
    String name = "java";
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    doNothing().when(tagService).updateProjectTag(eq(handle), eq(name), eq(request));

    mockMvc
        .perform(
            patch("/projects/{handle}/tags/{name}", handle, name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateProjectTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Update Project Tag")
                    .description("프로젝트 태그 정보를 수정합니다. 프로젝트 관리자만 접근할 수 있습니다.")
                    .requestSchema(schema(UpdateTagRequest.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("name").description("수정할 태그의 현재 이름")),
                UpdateTagRequestSnippets.getUpdateTagRequestFields()));
  }

  @Test
  @DisplayName("[updateProjectTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void updateProjectTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    mockMvc
        .perform(
            patch("/projects/{handle}/tags/{name}", "my-project", "java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[rejectTag] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void rejectTag() throws Exception {
    String name = "java";

    doNothing().when(tagService).rejectTag(name);

    mockMvc
        .perform(delete("/tags/{name}", name).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "RejectTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Reject Tag")
                    .description("전역 태그를 삭제합니다. 관리자만 접근할 수 있습니다."),
                preprocessRequest(RestDocsUtils.removeCsrfFormBody()),
                null,
                Function.identity(),
                pathParameters(parameterWithName("name").description("삭제할 태그 이름"))));
  }

  @Test
  @DisplayName("[rejectTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void rejectTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(delete("/tags/{name}", "java").with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[rejectProjectTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void rejectProjectTag() throws Exception {
    String handle = "my-project";
    String name = "java";

    doNothing().when(tagService).rejectProjectTag(handle, name);

    mockMvc
        .perform(delete("/projects/{handle}/tags/{name}", handle, name).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "RejectProjectTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Reject Project Tag")
                    .description("프로젝트 태그를 삭제합니다. 프로젝트 관리자만 접근할 수 있습니다."),
                preprocessRequest(RestDocsUtils.removeCsrfFormBody()),
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("handle").description("프로젝트 핸들"),
                    parameterWithName("name").description("삭제할 태그 이름"))));
  }

  @Test
  @DisplayName("[rejectProjectTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void rejectProjectTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(delete("/projects/{handle}/tags/{name}", "my-project", "java").with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[mergeTags] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void mergeTags() throws Exception {
    MergeTagsRequest request = new MergeTagsRequest(1L, 2L);

    doNothing().when(tagService).mergeTags(eq(request));

    mockMvc
        .perform(
            post("/tags/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "MergeTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Merge Tags")
                    .description("동일한 풀에 속한 두 태그를 하나로 병합합니다. 관리자만 접근할 수 있습니다.")
                    .requestSchema(schema(MergeTagsRequest.class.getSimpleName())),
                null,
                null,
                Function.identity()));
  }

  @Test
  @DisplayName("[mergeTags] 로그인하지 않은 사용자는 접근할 수 없다")
  void mergeTags_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    MergeTagsRequest request = new MergeTagsRequest(1L, 2L);

    mockMvc
        .perform(
            post("/tags/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}
