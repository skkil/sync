package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetCommentsResponseSnippets {

  public static GetCommentsResponse getGetCommentsResponse() {
    GetCommentsResponse.Author author =
        GetCommentsResponse.Author.builder()
            .id(1L)
            .handle("user-handle")
            .name("User Name")
            .profileImageUrl("http://example.com/profile.jpg")
            .isPostAuthor(false)
            .build();

    GetCommentsResponse.Comment comment =
        new GetCommentsResponse.Comment(
            1L,
            author,
            "Comment content",
            false,
            OffsetDateTime.parse("2026-01-01T00:00:00Z"),
            OffsetDateTime.parse("2026-01-01T00:00:00Z"));

    return new GetCommentsResponse(CursorPaginationResponseSnippets.of(List.of(comment)));
  }

  public static ResponseFieldsSnippet getCommentsResponseFields() {
    FieldDescriptors fields =
        new FieldDescriptors()
            .and(fieldWithPath("comments").type(JsonFieldType.OBJECT).description("Comments"))
            .and(
                CursorPaginationResponseSnippets.getCursorPaginationResponseFields("comments")
                    .getFieldDescriptors()
                    .toArray(org.springframework.restdocs.payload.FieldDescriptor[]::new));

    fields =
        fields.andWithPrefix(
            "comments.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Comment ID"),
            fieldWithPath(".content")
                .type(JsonFieldType.STRING)
                .description("Comment content")
                .optional(),
            fieldWithPath(".isDeleted").type(JsonFieldType.BOOLEAN).description("Is deleted"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update timestamp"));

    fields =
        fields.andWithPrefix(
            "comments.nodes[].content.author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author user ID"),
            fieldWithPath(".handle").type(JsonFieldType.STRING).description("Author handle"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Author name"),
            fieldWithPath(".profileImageUrl")
                .type(JsonFieldType.STRING)
                .description("Author profile image URL")
                .optional(),
            fieldWithPath(".isPostAuthor")
                .type(JsonFieldType.BOOLEAN)
                .description("Whether the author is the post author"));

    return responseFields(fields.getFieldDescriptors());
  }
}
