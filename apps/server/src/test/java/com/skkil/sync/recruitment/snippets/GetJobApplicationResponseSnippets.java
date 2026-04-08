package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.recruitment.dto.response.GetJobApplicationResponse;
import com.skkil.sync.recruitment.enums.JobApplicationStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetJobApplicationResponseSnippets {

  public static GetJobApplicationResponse getGetJobApplicationResponse() {
    GetJobApplicationResponse.Company company =
        GetJobApplicationResponse.Company.builder().id(1L).name("Company").build();

    GetJobApplicationResponse.JobDescription jobDescription =
        GetJobApplicationResponse.JobDescription.builder()
            .title("Job Title")
            .description("Job Description")
            .location("Location")
            .build();

    return GetJobApplicationResponse.builder()
        .id(1L)
        .status(JobApplicationStatus.APPLIED)
        .company(company)
        .jobDescription(jobDescription)
        .notes("Notes")
        .build();
  }

  public static ResponseFieldsSnippet getGetJobApplicationResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Application ID"),
        fieldWithPath("status").type(JsonFieldType.STRING).description("Application Status"),
        fieldWithPath("company").type(JsonFieldType.OBJECT).description("Company Information"),
        fieldWithPath("company.id").type(JsonFieldType.NUMBER).description("Company ID"),
        fieldWithPath("company.name").type(JsonFieldType.STRING).description("Company Name"),
        fieldWithPath("jobDescription")
            .type(JsonFieldType.OBJECT)
            .description("Job Description Information"),
        fieldWithPath("jobDescription.title").type(JsonFieldType.STRING).description("Job Title"),
        fieldWithPath("jobDescription.description")
            .type(JsonFieldType.STRING)
            .description("Job Description"),
        fieldWithPath("jobDescription.location")
            .type(JsonFieldType.STRING)
            .description("Job Location"),
        fieldWithPath("notes")
            .type(JsonFieldType.STRING)
            .description("Additional Notes")
            .optional());
  }
}
