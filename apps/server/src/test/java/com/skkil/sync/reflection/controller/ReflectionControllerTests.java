package com.skkil.sync.reflection.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import com.skkil.sync.reflection.dto.response.CreateReflectionResponse;
import com.skkil.sync.reflection.service.ReflectionService;
import com.skkil.sync.reflection.snippets.CreateReflectionRequestSnippets;
import com.skkil.sync.reflection.snippets.CreateReflectionResponseSnippets;
import com.skkil.sync.reflection.snippets.UpdateReflectionRequestSnippets;
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

@WebMvcTest(ReflectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private ReflectionService reflectionService;

  @Test
  @DisplayName("[createReflection] API 문서화 테스트")
  @WithAuthenticatedUser
  void createReflection() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateReflectionRequest request = CreateReflectionRequestSnippets.getCreateReflectionRequest();
    CreateReflectionResponse response =
        CreateReflectionResponseSnippets.getCreateReflectionResponse();

    when(reflectionService.createReflection(eq(user.userId()), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/reflections")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateReflection",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Create Reflection")
                    .description("Create Reflection")
                    .responseSchema(schema(CreateReflectionResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateReflectionRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                CreateReflectionRequestSnippets.getCreateReflectionRequestFields(),
                CreateReflectionResponseSnippets.getCreateReflectionResponseFields()));
  }

  @Test
  @DisplayName("[updateReflection] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateReflection() throws Exception {
    Long reflectionId = 1L;
    UpdateReflectionRequest request = UpdateReflectionRequestSnippets.getUpdateReflectionRequest();

    doNothing().when(reflectionService).updateReflection(eq(reflectionId), eq(request));

    mockMvc
        .perform(
            patch("/reflections/{reflectionId}", reflectionId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateReflection",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Update Reflection")
                    .description("Update Reflection")
                    .requestSchema(schema(UpdateReflectionRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                pathParameters(parameterWithName("reflectionId").description("Reflection ID")),
                UpdateReflectionRequestSnippets.getUpdateReflectionRequestFields()));
  }

  @Test
  @DisplayName("[deleteReflection] API 문서화 테스트")
  @WithAuthenticatedUser
  void deleteReflection() throws Exception {
    doNothing().when(reflectionService).deleteReflection(eq(1L));

    mockMvc
        .perform(delete("/reflections/{reflectionId}", 1L))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "DeleteReflection",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Delete Reflection")
                    .description("Delete Reflection"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("reflectionId").description("Reflection ID"))));
  }
}
