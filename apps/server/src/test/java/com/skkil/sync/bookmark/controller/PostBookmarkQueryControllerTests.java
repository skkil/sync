package com.skkil.sync.bookmark.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedPostsResponse;
import com.skkil.sync.bookmark.service.PostBookmarkQueryService;
import com.skkil.sync.bookmark.snippets.GetBookmarkedPostsResponseSnippets;
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

@WebMvcTest(PostBookmarkQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostBookmarkQueryControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostBookmarkQueryService postBookmarkQueryService;

  @Test
  @DisplayName("[getBookmarkedPosts] API 문서화 테스트")
  @WithAuthenticatedUser
  void getBookmarkedPosts() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetBookmarkedPostsResponse response =
        GetBookmarkedPostsResponseSnippets.getGetBookmarkedPostsResponse();

    when(postBookmarkQueryService.getBookmarkedPosts(eq(user.userId()), eq(pagination)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/bookmarks/posts")
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetBookmarkedPosts",
                ResourceSnippetParameters.builder()
                    .tag("bookmark")
                    .summary("Get Bookmarked Posts")
                    .description("Get Bookmarked Posts")
                    .responseSchema(schema(GetBookmarkedPostsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetBookmarkedPostsResponseSnippets.getBookmarkedPostsResponseFields()));
  }
}
