package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import com.skkil.sync.user.service.UserRelationshipService;
import com.skkil.sync.user.snippets.GetConnectionsResponseSnippets;
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

@WebMvcTest(UserRelationshipController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class UserRelationshipControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserRelationshipService userRelationshipService;

  @Test
  @DisplayName("[followUser] API 문서화 테스트")
  @WithAuthenticatedUser
  void followUser() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long followeeId = 2L;

    doNothing().when(userRelationshipService).followUser(eq(follower.userId()), eq(followeeId));

    mockMvc
        .perform(
            post("/users/follow/{followeeId}", followeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "FollowUser",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Follow User")
                    .description("사용자를 팔로우합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("followeeId").description("팔로우할 사용자 ID"))));
  }

  @Test
  @DisplayName("[unfollowUser] API 문서화 테스트")
  @WithAuthenticatedUser
  void unfollowUser() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long followeeId = 2L;

    doNothing().when(userRelationshipService).unfollowUser(eq(follower.userId()), eq(followeeId));

    mockMvc
        .perform(
            delete("/users/unfollow/{followeeId}", followeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnfollowUser",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Unfollow User")
                    .description("사용자 팔로우를 취소합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("followeeId").description("언팔로우할 사용자 ID"))));
  }

  @Test
  @DisplayName("[getFollowing] API 문서화 테스트")
  void getFollowing() throws Exception {
    Long userId = 1L;
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetConnectionsResponse response = GetConnectionsResponseSnippets.getGetConnectionsResponse();

    when(userRelationshipService.getFollowing(userId, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/users/{userId}/following", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetFollowing",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Get Following")
                    .description("사용자가 팔로우하는 사용자 목록을 조회합니다.")
                    .responseSchema(schema(GetConnectionsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("User ID")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetConnectionsResponseSnippets.getConnectionsResponseFields()));
  }

  @Test
  @DisplayName("[getFollowers] API 문서화 테스트")
  void getFollowers() throws Exception {
    Long userId = 1L;
    CursorPaginationRequest pagination =
        CursorPaginationRequestSnippets.getCursorPaginationRequest();
    GetConnectionsResponse response = GetConnectionsResponseSnippets.getGetConnectionsResponse();

    when(userRelationshipService.getFollowers(userId, pagination)).thenReturn(response);

    mockMvc
        .perform(
            get("/users/{userId}/followers", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParams(
                    CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetFollowers",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Get Followers")
                    .description("사용자를 팔로우하는 사용자 목록을 조회합니다.")
                    .responseSchema(schema(GetConnectionsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("userId").description("User ID")),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetConnectionsResponseSnippets.getConnectionsResponseFields()));
  }
}
