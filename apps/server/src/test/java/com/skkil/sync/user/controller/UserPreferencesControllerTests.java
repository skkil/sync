package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.dto.request.UpdateUserPreferencesRequest;
import com.skkil.sync.user.dto.response.GetUserPreferencesResponse;
import com.skkil.sync.user.service.UserPreferencesService;
import com.skkil.sync.user.snippets.GetUserPreferencesResponseSnippets;
import com.skkil.sync.user.snippets.UpdateUserPreferencesRequestSnippets;
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

@WebMvcTest(UserPreferencesController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class UserPreferencesControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private UserPreferencesService userPreferencesService;

  @Test
  @DisplayName("[getUserPreferences] API 문서화 테스트")
  @WithAuthenticatedUser
  void getUserPreferences() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetUserPreferencesResponse response =
        GetUserPreferencesResponseSnippets.getGetUserPreferencesResponse();

    when(userPreferencesService.getUserPreferences(eq(user.userId()))).thenReturn(response);

    mockMvc
        .perform(get("/preferences/me"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetUserPreferences",
                ResourceSnippetParameters.builder()
                    .tag("preferences")
                    .summary("Get User Preferences")
                    .description("Get User Preferences")
                    .responseSchema(schema(GetUserPreferencesResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                GetUserPreferencesResponseSnippets.getUserPreferencesResponseFields()));
  }

  @Test
  @DisplayName("[updateUserPreferences] API 문서화 테스트")
  @WithAuthenticatedUser
  void updateUserPreferences() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    UpdateUserPreferencesRequest request =
        UpdateUserPreferencesRequestSnippets.getUpdateUserPreferencesRequest();

    doNothing().when(userPreferencesService).updateUserPreferences(eq(user.userId()), eq(request));

    mockMvc
        .perform(
            patch("/preferences/me")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateUserPreferences",
                ResourceSnippetParameters.builder()
                    .tag("preferences")
                    .summary("Update User Preferences")
                    .description("Update User Preferences")
                    .requestSchema(schema(UpdateUserPreferencesRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                UpdateUserPreferencesRequestSnippets.getUpdateUserPreferencesRequestFields()));
  }
}
