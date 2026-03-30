package com.skkil.sync.provider.company.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.provider.company.dto.response.GetJobPostingResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetJobPostingResponseSnippets {

  public static GetJobPostingResponse getGetJobPostingResponse() {
    GetJobPostingResponse.Company company = new GetJobPostingResponse.Company("1", "Company");

    return new GetJobPostingResponse(
        "1",
        company,
        "Software Engineer",
        "Job Description",
        "Location",
        DateTimeTestUtils.defaultTestLocalDateTime(),
        DateTimeTestUtils.defaultTestLocalDateTime());
  }

  public static ResponseFieldsSnippet getJobPostingResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.STRING).description("Job Posting ID"),
        fieldWithPath("company").type(JsonFieldType.OBJECT).description("Company"),
        fieldWithPath("company.id").type(JsonFieldType.STRING).description("Company ID"),
        fieldWithPath("company.name").type(JsonFieldType.STRING).description("Company Name"),
        fieldWithPath("jobTitle").type(JsonFieldType.STRING).description("Job Title"),
        fieldWithPath("jobDescription").type(JsonFieldType.STRING).description("Job Description"),
        fieldWithPath("location").type(JsonFieldType.STRING).description("Location"),
        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Creation Timestamp"),
        fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("Update Timestamp"));
  }
}
