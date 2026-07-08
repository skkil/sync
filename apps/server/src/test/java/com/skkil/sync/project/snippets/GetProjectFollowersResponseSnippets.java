package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.project.dto.response.GetProjectFollowersResponse;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectFollowersResponseSnippets {

  public static GetProjectFollowersResponse getGetProjectFollowersResponse() {
    GetProjectFollowersResponse.Follower follower =
        new GetProjectFollowersResponse.Follower(UserSummarySnippets.getUserSummary());

    return new GetProjectFollowersResponse(CursorPaginationResponseSnippets.of(List.of(follower)));
  }

  public static ResponseFieldsSnippet getProjectFollowersResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("followers");

    fields =
        fields.andWithPrefix(
            "followers.nodes[].content",
            fieldWithPath(".user").type(JsonFieldType.OBJECT).description("팔로워 유저 정보"));

    fields =
        fields.andWithPrefix(
            "followers.nodes[].content.user",
            UserSummarySnippets.getUserSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
