package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;

public class CursorPaginationResponseSnippets {

  public static <T> CursorPaginationResponse<T> of(List<T> content) {
    CursorPaginationResponse.PageInfo pageInfo =
        CursorPaginationResponse.PageInfo.builder()
            .size(1)
            .hasNextPage(false)
            .hasPreviousPage(false)
            .startCursor("start-cursor")
            .endCursor("end-cursor")
            .build();

    return new CursorPaginationResponse<>(
        pageInfo,
        content.stream().map(node -> new CursorPaginationResponse.Node<>("cursor", node)).toList());
  }

  public static FieldDescriptors getCursorPaginationResponseFields(String pathPrefix) {
    FieldDescriptors fields = new FieldDescriptors();

    return fields.andWithPrefix(
        pathPrefix,
        fieldWithPath(".pageInfo").type(JsonFieldType.OBJECT).description("Page Info").attributes(),
        fieldWithPath(".pageInfo.size").type(JsonFieldType.NUMBER).description("Page Size"),
        fieldWithPath(".pageInfo.hasNextPage")
            .type(JsonFieldType.BOOLEAN)
            .description("Has Next Page"),
        fieldWithPath(".pageInfo.hasPreviousPage")
            .type(JsonFieldType.BOOLEAN)
            .description("Has Previous Page"),
        fieldWithPath(".pageInfo.startCursor")
            .type(JsonFieldType.STRING)
            .description("Start Cursor"),
        fieldWithPath(".pageInfo.endCursor").type(JsonFieldType.STRING).description("End Cursor"),
        fieldWithPath(".nodes").type(JsonFieldType.ARRAY).description("Nodes"),
        fieldWithPath(".nodes[].cursor").type(JsonFieldType.STRING).description("Node Cursor"),
        fieldWithPath(".nodes[].content")
            .type(JsonFieldType.OBJECT)
            .description("Node Content")
            .attributes());
  }
}
