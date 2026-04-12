package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.recruitment.dto.request.UploadJobApplicationFileRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UploadJobApplicationFileRequestSnippets {

  public static UploadJobApplicationFileRequest getUploadJobApplicationFileRequest() {
    return new UploadJobApplicationFileRequest(1L);
  }

  public static RequestFieldsSnippet getUploadJobApplicationFileRequestFields() {
    return requestFields(fieldWithPath("fileId").type(JsonFieldType.NUMBER).description("File ID"));
  }
}
