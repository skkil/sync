package com.skkil.sync.bookmark.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.service.ReflectionBookmarkService;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
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

@WebMvcTest(ReflectionBookmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionBookmarkControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReflectionBookmarkService reflectionBookmarkService;

  @Test
  @DisplayName("[bookmarkReflection] API 문서화 테스트")
  @WithAuthenticatedUser
  void bookmarkReflection() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long reflectionId = 2L;

    doNothing()
        .when(reflectionBookmarkService)
        .bookmarkReflection(eq(user.userId()), eq(reflectionId));

    mockMvc
        .perform(post("/reflections/{reflectionId}/bookmarks", reflectionId))
        .andExpect(status().isOk())
        .andDo(
            document(
                "BookmarkReflection",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Bookmark Reflection")
                    .description("Bookmark Reflection"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("reflectionId").description("Reflection ID"))));
  }

  @Test
  @DisplayName("[unbookmarkReflection] API 문서화 테스트")
  @WithAuthenticatedUser
  void unbookmarkReflection() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long reflectionId = 2L;

    doNothing()
        .when(reflectionBookmarkService)
        .unbookmarkReflection(eq(user.userId()), eq(reflectionId));

    mockMvc
        .perform(delete("/reflections/{reflectionId}/bookmarks", reflectionId))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnbookmarkReflection",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Unbookmark Reflection")
                    .description("Unbookmark Reflection"),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("reflectionId").description("Reflection ID"))));
  }
}
