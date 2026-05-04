package com.skkil.sync.reflection.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.reflection.dto.response.GetReflectionResponse;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import com.skkil.sync.reflection.service.ReflectionQueryService;
import com.skkil.sync.reflection.snippets.GetReflectionResponseSnippets;
import com.skkil.sync.reflection.snippets.GetReflectionsResponseSnippets;
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

@WebMvcTest(ReflectionQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReflectionQueryService reflectionQueryService;

  @Test
  @DisplayName("[getReflections] API 문서화 테스트")
  void getReflections() throws Exception {
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetReflectionsResponse response = GetReflectionsResponseSnippets.getGetReflectionsResponse();

    when(reflectionQueryService.getReflections(pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/reflections")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetReflections",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Get Reflections")
                    .description("Get Reflections")
                    .responseSchema(schema("GetReflectionsResponse")),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetReflectionsResponseSnippets.getReflectionsResponseFields()));
  }

  @Test
  @DisplayName("[getReflectionBySlug] API 문서화 테스트")
  void getReflectionBySlug() throws Exception {
    String slug = "test-slug";
    GetReflectionResponse response = GetReflectionResponseSnippets.getGetReflectionResponse();

    when(reflectionQueryService.getReflectionBySlug(slug)).thenReturn(response);

    mockMvc
        .perform(get("/reflections/{slug}", slug))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetReflectionBySlug",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Get Reflections")
                    .description("Get Reflections")
                    .responseSchema(schema(GetReflectionResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetReflectionResponseSnippets.getReflectionResponseFields()));
  }

  @Test
  @DisplayName("[getUserReflections] API 문서화 테스트")
  void getUserReflections() throws Exception {
    Long userId = 1L;
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetReflectionsResponse response = GetReflectionsResponseSnippets.getGetReflectionsResponse();

    when(reflectionQueryService.getUserReflections(userId, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/users/{userId}/reflections", userId)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetUserReflections",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Get Reflections")
                    .description("Get Reflections")
                    .responseSchema(schema("GetReflectionsResponse")),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("User ID")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetReflectionsResponseSnippets.getReflectionsResponseFields()));
  }

  @Test
  @DisplayName("[getProjectExperienceReflections] API 문서화 테스트")
  void getProjectExperienceReflections() throws Exception {
    Long projectExperienceId = 1L;
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetReflectionsResponse response = GetReflectionsResponseSnippets.getGetReflectionsResponse();

    when(reflectionQueryService.getProjectExperienceReflections(projectExperienceId, pagination))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/experiences/project/{experienceId}/reflections", projectExperienceId)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectExperienceReflections",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Get Reflections")
                    .description("Get Reflections")
                    .responseSchema(schema("GetReflectionsResponse")),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("experienceId").description("Project Experience ID")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetReflectionsResponseSnippets.getReflectionsResponseFields()));
  }
}
