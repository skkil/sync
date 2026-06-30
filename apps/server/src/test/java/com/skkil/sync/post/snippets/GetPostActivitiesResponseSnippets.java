package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostActivitiesResponseSnippets {

  public static GetPostActivitiesResponse getGetPostActivitiesResponse() {
    GetPostActivitiesResponse.Activity activity1 =
        GetPostActivitiesResponse.Activity.builder()
            .date(LocalDate.of(2024, 1, 1))
            .count(5L)
            .build();

    GetPostActivitiesResponse.Activity activity2 =
        GetPostActivitiesResponse.Activity.builder()
            .date(LocalDate.of(2024, 1, 2))
            .count(3L)
            .build();

    return new GetPostActivitiesResponse(List.of(activity1, activity2));
  }

  public static ResponseFieldsSnippet getPostActivitiesResponseFields() {
    return responseFields(
        fieldWithPath("activities").type(JsonFieldType.ARRAY).description("List of Activities"),
        fieldWithPath("activities[].date").type(JsonFieldType.STRING).description("Activity Date"),
        fieldWithPath("activities[].count")
            .type(JsonFieldType.NUMBER)
            .description("Number of Posts"));
  }
}
