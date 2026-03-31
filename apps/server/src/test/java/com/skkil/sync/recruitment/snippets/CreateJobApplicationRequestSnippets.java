package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.recruitment.dto.request.CreateJobApplicationRequest;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateJobApplicationRequestSnippets {

  public static CreateJobApplicationRequest getCreateJobApplicationRequest() {
    return new CreateJobApplicationRequest("1", "1");
  }

  public static RequestFieldsSnippet getCreateJobApplicationRequestFields() {
    return requestFields(
        fieldWithPath("companyId")
            .type(org.springframework.restdocs.payload.JsonFieldType.STRING)
            .description("Company ID"),
        fieldWithPath("jobPostingId")
            .type(org.springframework.restdocs.payload.JsonFieldType.STRING)
            .description("Job Posting ID"));
  }
}
