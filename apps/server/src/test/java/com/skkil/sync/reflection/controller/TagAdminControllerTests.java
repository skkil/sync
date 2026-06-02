package com.skkil.sync.reflection.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.reflection.dto.request.UpdateTagRequest;
import com.skkil.sync.reflection.dto.response.GetTagsResponse;
import com.skkil.sync.reflection.service.TagService;
import com.skkil.sync.reflection.snippets.GetTagsResponseSnippets;
import com.skkil.sync.reflection.snippets.UpdateTagRequestSnippets;
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

@WebMvcTest(TagAdminController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TagAdminControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private TagService tagService;

  @Test
  @DisplayName("[getAllTags] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void getAllTags() throws Exception {
    GetTagsResponse response = GetTagsResponseSnippets.getGetTagsResponse();

    when(tagService.getAllTags()).thenReturn(response);

    mockMvc
        .perform(get("/admin/tags"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetAllTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get All Tags")
                    .description("모든 태그를 조회합니다. (관리자 전용)")
                    .responseSchema(schema(GetTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetTagsResponseSnippets.getGetTagsResponseFields()));
  }

  @Test
  @DisplayName("[getAllTags] 로그인하지 않은 사용자는 접근할 수 없다")
  void getAllTags_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/admin/tags")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getAllTags] ADMIN 권한이 없는 사용자는 접근할 수 없다")
  @WithAuthenticatedUser(role = Role.USER)
  void getAllTags_nonAdminUser_shouldReturnForbidden() throws Exception {
    mockMvc.perform(get("/admin/tags")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("[updateTag] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void updateTag() throws Exception {
    Long tagId = 1L;
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    doNothing().when(tagService).updateTag(tagId, request);

    mockMvc
        .perform(
            patch("/admin/tags/{tagId}", tagId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf().asHeader()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Update Tag")
                    .description("태그 정보를 수정합니다. (관리자 전용)")
                    .requestSchema(schema(UpdateTagRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                pathParameters(parameterWithName("tagId").description("태그 ID")),
                UpdateTagRequestSnippets.getUpdateTagRequestFields()));
  }

  @Test
  @DisplayName("[updateTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void updateTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    mockMvc
        .perform(
            patch("/admin/tags/{tagId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[updateTag] ADMIN 권한이 없는 사용자는 접근할 수 없다")
  @WithAuthenticatedUser(role = Role.USER)
  void updateTag_nonAdminUser_shouldReturnForbidden() throws Exception {
    UpdateTagRequest request = UpdateTagRequestSnippets.getUpdateTagRequest();

    mockMvc
        .perform(
            patch("/admin/tags/{tagId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("[deleteTag] API 문서화 테스트")
  @WithAuthenticatedUser(role = Role.ADMIN)
  void deleteTag() throws Exception {
    Long tagId = 1L;

    doNothing().when(tagService).deleteTag(tagId);

    mockMvc
        .perform(delete("/admin/tags/{tagId}", tagId).with(csrf().asHeader()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeleteTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Delete Tag")
                    .description("태그를 삭제합니다. (관리자 전용)"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("tagId").description("태그 ID"))));
  }

  @Test
  @DisplayName("[deleteTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void deleteTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(delete("/admin/tags/{tagId}", 1L).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[deleteTag] ADMIN 권한이 없는 사용자는 접근할 수 없다")
  @WithAuthenticatedUser(role = Role.USER)
  void deleteTag_nonAdminUser_shouldReturnForbidden() throws Exception {
    mockMvc
        .perform(delete("/admin/tags/{tagId}", 1L).with(csrf()))
        .andExpect(status().isForbidden());
  }
}
