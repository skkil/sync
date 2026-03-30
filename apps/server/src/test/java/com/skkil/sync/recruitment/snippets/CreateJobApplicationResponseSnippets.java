package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.recruitment.dto.response.CreateJobApplicationResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateJobApplicationResponseSnippets {

  public static CreateJobApplicationResponse getCreateJobApplicationResponse() {
    return new CreateJobApplicationResponse(1L);
  }

  public static ResponseFieldsSnippet getCreateJobApplicationResponseFields() {
    return responseFields(
        fieldWithPath("applicationId")
            .type(JsonFieldType.NUMBER)
            .description("Job Application ID"));
  }
}
