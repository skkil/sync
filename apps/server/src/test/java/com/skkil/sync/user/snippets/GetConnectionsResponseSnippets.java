package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetConnectionsResponseSnippets {

  public static GetConnectionsResponse getGetConnectionsResponse() {
    UserSummary summary = UserSummarySnippets.getUserSummary();

    return new GetConnectionsResponse(CursorPaginationResponseSnippets.of(List.of(summary)));
  }

  public static ResponseFieldsSnippet getConnectionsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("connections");

    fields =
        fields.andWithPrefix(
            "connections.nodes[].content",
            UserSummarySnippets.getUserSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
