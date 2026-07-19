package com.skkil.sync.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.project.dto.response.GetProjectRecommendationsResponse;
import com.skkil.sync.project.model.ProjectRecommendationType;
import com.skkil.sync.project.service.ProjectRecommendationService;
import com.skkil.sync.project.snippets.GetProjectRecommendationsResponseSnippets;
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

@WebMvcTest(ProjectRecommendationController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProjectRecommendationControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProjectRecommendationService projectRecommendationService;

  @Test
  @DisplayName("[getProjectRecommendations] API 문서화 테스트")
  @WithAuthenticatedUser
  void getProjectRecommendations() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetProjectRecommendationsResponse response =
        GetProjectRecommendationsResponseSnippets.getGetProjectRecommendationsResponse();

    when(projectRecommendationService.getRecommendations(eq(user.userId()), isNull()))
        .thenReturn(response);

    mockMvc
        .perform(get("/projects/recommendations"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProjectRecommendations",
                ResourceSnippetParameters.builder()
                    .tag("project")
                    .summary("Get Project Recommendations")
                    .description("팔로우할 만한 프로젝트 목록을 추천합니다.")
                    .responseSchema(
                        schema(GetProjectRecommendationsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("type").description("추천 프로젝트 종류").optional()),
                GetProjectRecommendationsResponseSnippets
                    .getGetProjectRecommendationsResponseFields()));
  }

  @Test
  @DisplayName("[getProjectRecommendations] type 파라미터가 주어지면 해당 추천 채널만 사용한다")
  @WithAuthenticatedUser
  void getProjectRecommendations_withType_shouldUseOnlyThatChannel() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetProjectRecommendationsResponse response =
        GetProjectRecommendationsResponseSnippets.getGetProjectRecommendationsResponse();

    when(projectRecommendationService.getRecommendations(
            eq(user.userId()), eq(ProjectRecommendationType.TRENDING)))
        .thenReturn(response);

    mockMvc
        .perform(get("/projects/recommendations").queryParam("type", "TRENDING"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("[getProjectRecommendations] 로그인하지 않은 사용자는 접근할 수 없다")
  void getProjectRecommendations_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/projects/recommendations")).andExpect(status().isUnauthorized());
  }
}
