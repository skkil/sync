package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectHandleAvailabilityResponseSnippets {

  public static GetProjectHandleAvailabilityResponse getGetProjectHandleAvailabilityResponse() {
    return new GetProjectHandleAvailabilityResponse(true);
  }

  public static ResponseFieldsSnippet getResponseFields() {
    return responseFields(
        fieldWithPath("available").type(JsonFieldType.BOOLEAN).description("핸들 사용 가능 여부"));
  }
}
