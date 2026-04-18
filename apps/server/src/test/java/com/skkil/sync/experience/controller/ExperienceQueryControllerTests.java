package com.skkil.sync.experience.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.experience.dto.response.GetProjectExperiencesResponse;
import com.skkil.sync.experience.service.ExperienceQueryService;
import com.skkil.sync.experience.snippets.GetProjectExperiencesResponseSnippets;
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

@WebMvcTest(ExperienceQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ExperienceQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ExperienceQueryService experienceQueryService;

  @Test
  @DisplayName("[getProjectExperiences] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectExperiences() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long userId = 1L;
    GetProjectExperiencesResponse response =
        GetProjectExperiencesResponseSnippets.getGetProjectExperiencesResponse();

    when(experienceQueryService.getProjectExperiences(eq(user.userId()), eq(userId)))
        .thenReturn(response);

    mockMvc
        .perform(get("/profiles/{userId}/experiences/projects", userId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectExperiences",
                ResourceSnippetParameters.builder()
                    .tag("experiences")
                    .summary("Get Project Experiences")
                    .description("Get project experiences for a user profile")
                    .responseSchema(schema(GetProjectExperiencesResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("User ID")),
                GetProjectExperiencesResponseSnippets
                    .getGetProjectExperiencesResponseFieldsSnippet()));
  }
}
