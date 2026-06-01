package com.skkil.sync.badge.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.badge.dto.response.GetBadgeCollectionsResponse;
import com.skkil.sync.badge.service.BadgeCollectionQueryService;
import com.skkil.sync.badge.snippets.GetBadgeCollectionsResponseSnippets;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.config.SecurityConfig;
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

@WebMvcTest(BadgeCollectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class BadgeCollectionControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private BadgeCollectionQueryService badgeCollectionQueryService;

  @Test
  @DisplayName("[getBadgeCollections] API 문서화 테스트")
  void getBadgeCollections() throws Exception {
    Long userId = 1L;
    GetBadgeCollectionsResponse response =
        GetBadgeCollectionsResponseSnippets.getGetBadgeCollectionsResponse();

    when(badgeCollectionQueryService.getBadgeCollections(
            isNull(AuthenticatedUser.class), eq(userId)))
        .thenReturn(response);

    mockMvc
        .perform(get("/users/{userId}/badges", userId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetBadgeCollections",
                ResourceSnippetParameters.builder()
                    .tag("badge")
                    .summary("Get Badge Collections")
                    .description("유저의 뱃지 도감을 조회합니다.")
                    .responseSchema(schema(GetBadgeCollectionsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("유저 ID")),
                GetBadgeCollectionsResponseSnippets.getBadgeCollectionsResponseFields()));
  }
}
