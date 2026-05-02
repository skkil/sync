package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationResponseSnippets;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetJobApplicationsResponseSnippets {

  public static GetJobApplicationsResponse getGetJobApplicationsResponse() {
    GetJobApplicationsResponse.Company company =
        new GetJobApplicationsResponse.Company(1L, "Company");

    GetJobApplicationsResponse.Application application =
        new GetJobApplicationsResponse.Application(1L, company);

    return new GetJobApplicationsResponse(
        OffsetPaginationResponseSnippets.of(List.of(application)));
  }

  public static ResponseFieldsSnippet getJobApplicationsResponseFields() {
    FieldDescriptors fields =
        OffsetPaginationResponseSnippets.getPaginationResponseFields("applications");

    fields =
        fields.andWithPrefix(
            "applications.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Application ID"));

    fields =
        fields.andWithPrefix(
            "applications.content[].company",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Company ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Company Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
