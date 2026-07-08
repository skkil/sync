package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.user.dto.response.SearchUsersResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchUsersResponseSnippets {

  public static SearchUsersResponse getSearchUsersResponse() {
    return new SearchUsersResponse(List.of(UserSummarySnippets.getUserSummary()));
  }

  public static ResponseFieldsSnippet getSearchUsersResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("users").type(JsonFieldType.ARRAY).description("사용자 목록"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields("users[]."));

    return responseFields(fields.toArray(new FieldDescriptor[0]));
  }
}
