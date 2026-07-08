package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.user.dto.response.GetUserRecommendationsResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetUserRecommendationsResponseSnippets {

  public static GetUserRecommendationsResponse getGetUserRecommendationsResponse() {
    GetUserRecommendationsResponse.User user =
        new GetUserRecommendationsResponse.User("1", UserSummarySnippets.getUserSummary());

    return new GetUserRecommendationsResponse(List.of(user));
  }

  public static ResponseFieldsSnippet getGetUserRecommendationsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("users").type(JsonFieldType.ARRAY).description("추천 사용자 목록"));
    fields.add(fieldWithPath("users[].userId").type(JsonFieldType.STRING).description("사용자 ID"));
    fields.add(
        fieldWithPath("users[].summary").type(JsonFieldType.OBJECT).description("사용자 요약 정보"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields("users[].summary."));

    return responseFields(fields.toArray(new FieldDescriptor[0]));
  }
}
