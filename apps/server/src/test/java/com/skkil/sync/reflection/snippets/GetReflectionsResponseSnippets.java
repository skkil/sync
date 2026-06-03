package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetReflectionsResponseSnippets {

  public static GetReflectionsResponse getGetReflectionsResponse() {
    GetReflectionsResponse.Author author = new GetReflectionsResponse.Author(1L, "User");
    GetReflectionsResponse.Project project = new GetReflectionsResponse.Project(1L, "Project");

    GetReflectionsResponse.Reflection reflection =
        new GetReflectionsResponse.Reflection(
            1L,
            author,
            project,
            "Reflection Content",
            DateTimeTestUtils.defaultTestLocalDateTime());

    return new GetReflectionsResponse(CursorPaginationResponseSnippets.of(List.of(reflection)));
  }

  public static ResponseFieldsSnippet getReflectionsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("reflections");

    fields =
        fields.andWithPrefix(
            "reflections.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Reflection ID"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Reflection Content"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation Timestamp"));

    fields =
        fields.andWithPrefix(
            "reflections.nodes[].content.author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author User ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Author Name"));

    fields =
        fields.andWithPrefix(
            "reflections.nodes[].content.project",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Project ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Project Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
