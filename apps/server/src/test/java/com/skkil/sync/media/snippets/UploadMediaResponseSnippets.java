package com.skkil.sync.media.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.media.dto.response.UploadMediaResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class UploadMediaResponseSnippets {

  public static UploadMediaResponse getUploadMediaResponse() {
    return UploadMediaResponse.builder()
        .mediaId("1")
        .uploadUrl("https://example.com/upload")
        .expiresAt(DateTimeTestUtils.defaultTestLocalDateTime())
        .build();
  }

  public static ResponseFieldsSnippet getUploadMediaResponseFields() {
    return responseFields(
        fieldWithPath("mediaId").type(JsonFieldType.STRING).description("Media ID"),
        fieldWithPath("uploadUrl").type(JsonFieldType.STRING).description("Pre-signed Upload URL"),
        fieldWithPath("expiresAt").type(JsonFieldType.STRING).description("Expiration Time"));
  }
}
