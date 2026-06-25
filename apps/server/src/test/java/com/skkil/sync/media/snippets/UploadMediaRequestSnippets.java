package com.skkil.sync.media.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.media.dto.request.UploadMediaRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UploadMediaRequestSnippets {

  public static UploadMediaRequest getUploadMediaRequest() {
    return UploadMediaRequest.builder()
        .mediaType("image/jpeg")
        .fileName("test.jpg")
        .fileSize(100L)
        .build();
  }

  public static RequestFieldsSnippet getUploadMediaRequestFields() {
    return requestFields(
        fieldWithPath("mediaType").type(JsonFieldType.STRING).description("Media Type"),
        fieldWithPath("fileName").type(JsonFieldType.STRING).description("File Name"),
        fieldWithPath("fileSize").type(JsonFieldType.NUMBER).description("File Size"));
  }
}
