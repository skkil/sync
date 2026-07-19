package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
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
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.response.GetFollowedTagsResponse;
import com.skkil.sync.post.service.TagFollowService;
import com.skkil.sync.post.snippets.GetFollowedTagsResponseSnippets;
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

@WebMvcTest(TagFollowController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TagFollowControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TagFollowService tagFollowService;

  @Test
  @DisplayName("[followTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void followTag() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long tagId = 1L;

    doNothing().when(tagFollowService).followTag(eq(follower.userId()), eq(tagId));

    mockMvc
        .perform(
            post("/tags/{tagId}/follow", tagId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "FollowTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Follow Tag")
                    .description("전역 태그를 팔로우합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("tagId").description("팔로우할 태그 ID"))));
  }

  @Test
  @DisplayName("[followTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void followTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            post("/tags/{tagId}/follow", 1L).contentType(MediaType.APPLICATION_JSON).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[unfollowTag] API 문서화 테스트")
  @WithAuthenticatedUser
  void unfollowTag() throws Exception {
    AuthenticatedUser follower = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    Long tagId = 1L;

    doNothing().when(tagFollowService).unfollowTag(eq(follower.userId()), eq(tagId));

    mockMvc
        .perform(
            delete("/tags/{tagId}/unfollow", tagId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UnfollowTag",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Unfollow Tag")
                    .description("전역 태그 팔로우를 취소합니다."),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("tagId").description("언팔로우할 태그 ID"))));
  }

  @Test
  @DisplayName("[unfollowTag] 로그인하지 않은 사용자는 접근할 수 없다")
  void unfollowTag_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            delete("/tags/{tagId}/unfollow", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[getFollowedTags] API 문서화 테스트")
  void getFollowedTags() throws Exception {
    String handle = "john";
    GetFollowedTagsResponse response = GetFollowedTagsResponseSnippets.getGetFollowedTagsResponse();

    when(tagFollowService.getFollowedTags(eq(handle), any())).thenReturn(response);

    mockMvc
        .perform(
            get("/users/{handle}/followed-tags", handle)
                .queryParams(OffsetPaginationRequestSnippets.getPaginationRequestQueryParams()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetFollowedTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Get Followed Tags")
                    .description("유저가 팔로우하는 전역 태그 목록을 페이지 단위로 조회합니다.")
                    .responseSchema(schema(GetFollowedTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("유저 핸들")),
                OffsetPaginationRequestSnippets.getPaginationRequestParameters(),
                GetFollowedTagsResponseSnippets.getGetFollowedTagsResponseFields()));
  }
}
