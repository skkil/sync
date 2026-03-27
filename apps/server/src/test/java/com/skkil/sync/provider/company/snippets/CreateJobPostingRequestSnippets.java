package com.skkil.sync.provider.company.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateJobPostingRequestSnippets {

  public static CreateJobPostingRequest getCreateJobPostingRequest() {
    return new CreateJobPostingRequest("Job Title", "Job Description", "Location");
  }

  public static RequestFieldsSnippet getCreateJobPostingRequestFields() {
    return requestFields(
        fieldWithPath("jobTitle").type(JsonFieldType.STRING).description("Job Title"),
        fieldWithPath("jobDescription").type(JsonFieldType.STRING).description("Job Description"),
        fieldWithPath("location").type(JsonFieldType.STRING).description("Location").optional());
  }
}
