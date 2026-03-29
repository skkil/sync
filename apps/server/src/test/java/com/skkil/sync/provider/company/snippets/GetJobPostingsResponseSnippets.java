package com.skkil.sync.provider.company.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.dto.response.PaginationResponse;
import com.skkil.sync.common.util.pagination.snippets.PaginationResponseSnippets;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetJobPostingsResponseSnippets {

  public static GetJobPostingsResponse getGetJobPostingsResponse() {
    GetJobPostingsResponse.Company company = new GetJobPostingsResponse.Company("1", "Company");
    GetJobPostingsResponse.JobPosting jobPosting =
        new GetJobPostingsResponse.JobPosting(
            "1",
            company,
            "Software Engineer",
            "Job Description",
            "Location",
            DateTimeTestUtils.defaultTestLocalDateTime(),
            DateTimeTestUtils.defaultTestLocalDateTime());

    return new GetJobPostingsResponse(
        PaginationResponse.<GetJobPostingsResponse.JobPosting>builder()
            .content(List.of(jobPosting))
            .hasNext(true)
            .hasPrevious(false)
            .build());
  }

  public static ResponseFieldsSnippet getJobPostingsResponseFields() {
    FieldDescriptors fields = PaginationResponseSnippets.getPaginationResponseFields("postings");

    fields =
        fields.andWithPrefix(
            "postings.content[]",
            fieldWithPath(".id").type(JsonFieldType.STRING).description("Job Posting ID"),
            fieldWithPath(".company").type(JsonFieldType.OBJECT).description("Company"),
            fieldWithPath(".jobTitle").type(JsonFieldType.STRING).description("Job Title"),
            fieldWithPath(".jobDescription")
                .type(JsonFieldType.STRING)
                .description("Job Description"),
            fieldWithPath(".location").type(JsonFieldType.STRING).description("Location"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation Timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update Timestamp"));

    fields =
        fields.andWithPrefix(
            "postings.content[].company",
            fieldWithPath(".id").type(JsonFieldType.STRING).description("Company ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Company Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
