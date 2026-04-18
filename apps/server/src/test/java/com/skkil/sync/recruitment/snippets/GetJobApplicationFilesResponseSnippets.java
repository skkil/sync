package com.skkil.sync.recruitment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.recruitment.dto.response.GetJobApplicationFilesResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetJobApplicationFilesResponseSnippets {

  public static GetJobApplicationFilesResponse getGetJobApplicationFilesResponse() {
    GetJobApplicationFilesResponse.JobApplicationFile file =
        new GetJobApplicationFilesResponse.JobApplicationFile("https://example.com/file1.pdf");

    return new GetJobApplicationFilesResponse(List.of(file));
  }

  public static ResponseFieldsSnippet getGetJobApplicationFilesResponseFields() {
    return responseFields(
        fieldWithPath("files")
            .type(JsonFieldType.ARRAY)
            .description("List of job application files"),
        fieldWithPath("files[].url").type(JsonFieldType.STRING).description("File URL"));
  }
}
