package com.skkil.sync.bookmark.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedReflectionsResponse;
import com.skkil.sync.bookmark.service.ReflectionBookmarkQueryService;
import com.skkil.sync.bookmark.snippets.GetBookmarkedReflectionsResponseSnippets;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
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

@WebMvcTest(ReflectionBookmarkQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionBookmarkQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReflectionBookmarkQueryService reflectionBookmarkQueryService;

  @Test
  @DisplayName("[getBookmarkedReflections] API 문서화 테스트")
  @WithAuthenticatedUser
  void getBookmarkedReflections() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetBookmarkedReflectionsResponse response =
        GetBookmarkedReflectionsResponseSnippets.getGetBookmarkedReflectionsResponse();

    when(reflectionBookmarkQueryService.getBookmarkedReflections(eq(user.userId()), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/bookmarks/reflections")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetBookmarkedReflections",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Get Bookmarked Reflections")
                    .description("Get Bookmarked Reflections")
                    .responseSchema(schema(GetBookmarkedReflectionsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetBookmarkedReflectionsResponseSnippets.getBookmarkedReflectionsResponseFields()));
  }
}
