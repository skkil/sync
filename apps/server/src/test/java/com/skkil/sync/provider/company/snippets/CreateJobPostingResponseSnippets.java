package com.skkil.sync.provider.company.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateJobPostingResponseSnippets {

  public static CreateJobPostingResponse getCreateJobPostingResponse() {
    return new CreateJobPostingResponse("1");
  }

  public static ResponseFieldsSnippet getCreateJobPostingResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.STRING).description("Job Posting ID"));
  }
}
