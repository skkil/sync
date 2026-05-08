package com.skkil.sync.like.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.like.dto.request.CreateLikeRequest;
import com.skkil.sync.like.dto.response.CreateLikeResponse;
import com.skkil.sync.like.service.LikeService;
import com.skkil.sync.like.snippets.CreateLikeRequestSnippets;
import com.skkil.sync.like.snippets.CreateLikeResponseSnippets;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
class LikeControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private LikeService likeService;

  @Test
  @DisplayName("[createLike] API 문서화 테스트")
  @WithAuthenticatedUser
  void createLike() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateLikeRequest request = CreateLikeRequestSnippets.getCreateLikeRequest();
    CreateLikeResponse response = CreateLikeResponseSnippets.getCreateLikeResponse();

    when(likeService.like(eq(user.userId()), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "CreateLike",
                ResourceSnippetParameters.builder()
                    .tag("like")
                    .summary("Create Like")
                    .description("Create Like")
                    .responseSchema(schema(CreateLikeResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateLikeRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                CreateLikeRequestSnippets.getCreateLikeRequestFields(),
                CreateLikeResponseSnippets.getCreateLikeResponseFields()));
  }

  @Test
  @DisplayName("[createLike] 비로그인 사용자는 로그인 필요 메시지 반환")
  void createLike_unauthenticated() throws Exception {
    CreateLikeRequest request = CreateLikeRequestSnippets.getCreateLikeRequest();

    mockMvc
        .perform(
            post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("로그인이 필요한 기능입니다."));
  }

  @Test
  @DisplayName("[createLike] 중복 좋아요 insert 예외는 성공 응답으로 변환")
  @WithAuthenticatedUser
  void createLike_duplicateInsert_returnsSuccess() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateLikeRequest request = CreateLikeRequestSnippets.getCreateLikeRequest();
    CreateLikeResponse response = CreateLikeResponseSnippets.getCreateLikeResponse();

    when(likeService.like(eq(user.userId()), eq(request)))
        .thenThrow(new DataIntegrityViolationException("duplicate like"));
    when(likeService.hasLiked(eq(user.userId()), eq(request))).thenReturn(true);
    when(likeService.getLikedResponse(eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.liked").value(true))
        .andExpect(jsonPath("$.likeCount").value(1));
  }

  @Test
  @DisplayName("[deleteLike] 내가 누른 좋아요 삭제")
  @WithAuthenticatedUser
  void deleteLike() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    CreateLikeRequest request = CreateLikeRequestSnippets.getCreateLikeRequest();
    CreateLikeResponse response = new CreateLikeResponse(0L, false);

    when(likeService.unlike(eq(user.userId()), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            delete("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.liked").value(false))
        .andExpect(jsonPath("$.likeCount").value(0))
        .andDo(
            document(
                "DeleteLike",
                ResourceSnippetParameters.builder()
                    .tag("like")
                    .summary("Delete Like")
                    .description("Delete Like")
                    .responseSchema(schema(CreateLikeResponse.class.getSimpleName()))
                    .requestSchema(schema(CreateLikeRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                CreateLikeRequestSnippets.getCreateLikeRequestFields(),
                CreateLikeResponseSnippets.getCreateLikeResponseFields()));
  }

  @Test
  @DisplayName("[deleteLike] 비로그인 사용자는 로그인 필요 메시지 반환")
  void deleteLike_unauthenticated() throws Exception {
    CreateLikeRequest request = CreateLikeRequestSnippets.getCreateLikeRequest();

    mockMvc
        .perform(
            delete("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("로그인이 필요한 기능입니다."));
  }
}
