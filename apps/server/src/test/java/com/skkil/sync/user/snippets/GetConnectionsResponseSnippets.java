package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetConnectionsResponseSnippets {

  public static GetConnectionsResponse getGetConnectionsResponse() {
    GetConnectionsResponse.Connection connection =
        new GetConnectionsResponse.Connection("1", UserSummarySnippets.getUserSummary());

    return new GetConnectionsResponse(CursorPaginationResponseSnippets.of(List.of(connection)));
  }

  public static ResponseFieldsSnippet getConnectionsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("connections");

    fields =
        fields.andWithPrefix(
            "connections.nodes[].content",
            fieldWithPath(".userId").type(JsonFieldType.STRING).description("User ID"),
            fieldWithPath(".summary").type(JsonFieldType.OBJECT).description("User Summary"));

    fields =
        fields.andWithPrefix(
            "connections.nodes[].content.summary",
            UserSummarySnippets.getUserSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
