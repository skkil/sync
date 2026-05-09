package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.GetReflectionActivitiesResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetReflectionActivitiesResponseSnippets {

  public static GetReflectionActivitiesResponse getGetReflectionActivitiesResponse() {
    GetReflectionActivitiesResponse.Activity activity1 =
        GetReflectionActivitiesResponse.Activity.builder()
            .date(LocalDate.of(2024, 1, 1))
            .count(5L)
            .build();

    GetReflectionActivitiesResponse.Activity activity2 =
        GetReflectionActivitiesResponse.Activity.builder()
            .date(LocalDate.of(2024, 1, 2))
            .count(3L)
            .build();

    return new GetReflectionActivitiesResponse(List.of(activity1, activity2));
  }

  public static ResponseFieldsSnippet getReflectionActivitiesResponseFields() {
    return responseFields(
        fieldWithPath("activities").type(JsonFieldType.ARRAY).description("List of Activities"),
        fieldWithPath("activities[].date").type(JsonFieldType.STRING).description("Activity Date"),
        fieldWithPath("activities[].count")
            .type(JsonFieldType.NUMBER)
            .description("Number of Reflections"));
  }
}
