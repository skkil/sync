package com.skkil.sync.media.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.media.constant.MediaContext;
import com.skkil.sync.media.dto.request.UploadMediaRequest;
import com.skkil.sync.media.dto.response.UploadMediaResponse;
import com.skkil.sync.media.service.MediaService;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class MediaControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private MediaService mediaService;

  @Test
  @WithAuthenticatedUser
  void uploadMedia() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();

    UploadMediaRequest request = createUploadMediaRequest();
    UploadMediaResponse response = createUploadMediaResponse();

    when(mediaService.uploadMedia(eq(user.userId()), eq(request))).thenReturn(response);

    mockMvc
        .perform(
            post("/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsBytes(request))
                .with(csrf()))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "UploadMedia",
                ResourceSnippetParameters.builder()
                    .tag("media")
                    .summary("Upload Media")
                    .description("Upload Media")
                    .requestSchema(schema(UploadMediaRequest.class.getSimpleName()))
                    .responseSchema(schema(UploadMediaResponse.class.getSimpleName())),
                // TODO: Temporary fix for Orval's issue where it doesn't recognize the content type
                // if it contains a charset=UTF-8.
                // See https://github.com/orval-labs/orval/issues/3040
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                uploadMediaRequestFields(),
                uploadMediaResponseFields()));
  }

  private UploadMediaRequest createUploadMediaRequest() {
    return UploadMediaRequest.builder()
        .mediaType("image/jpeg")
        .mediaContext(MediaContext.PROFILE_IMAGE)
        .fileName("test.jpg")
        .fileSize(100L)
        .build();
  }

  private UploadMediaResponse createUploadMediaResponse() {
    return UploadMediaResponse.builder()
        .mediaId(1L)
        .uploadUrl("https://example.com/upload")
        .expiresAt(DateTimeTestUtils.defaultTestLocalDateTime())
        .build();
  }

  private RequestFieldsSnippet uploadMediaRequestFields() {
    return requestFields(
        fieldWithPath("mediaType").type(JsonFieldType.STRING).description("Media Type"),
        fieldWithPath("mediaContext").type(JsonFieldType.STRING).description("Media Context"),
        fieldWithPath("fileName").type(JsonFieldType.STRING).description("File Name"),
        fieldWithPath("fileSize").type(JsonFieldType.NUMBER).description("File Size"));
  }

  private ResponseFieldsSnippet uploadMediaResponseFields() {
    return responseFields(
        fieldWithPath("mediaId").type(JsonFieldType.NUMBER).description("Media ID"),
        fieldWithPath("uploadUrl").type(JsonFieldType.STRING).description("Pre-signed Upload URL"),
        fieldWithPath("expiresAt").type(JsonFieldType.STRING).description("Expiration Time"));
  }
}
