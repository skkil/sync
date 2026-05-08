package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetCommentsResponseSnippets {

  public static GetCommentsResponse getGetCommentsResponse() {
    GetCommentsResponse.Author author = new GetCommentsResponse.Author(1L, "user-handle");
    GetCommentsResponse.Comment reply =
        new GetCommentsResponse.Comment(
            2L,
            author,
            "Reply content",
            false,
            0L,
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-01-01T00:00:00Z"),
            List.of());
    GetCommentsResponse.Comment comment =
        new GetCommentsResponse.Comment(
            1L,
            author,
            "Comment content",
            false,
            1L,
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-01-01T00:00:00Z"),
            List.of(reply));

    return new GetCommentsResponse(List.of(comment));
  }

  public static ResponseFieldsSnippet getCommentsResponseFields() {
    FieldDescriptors fields =
        new FieldDescriptors()
            .and(fieldWithPath("comments").type(JsonFieldType.ARRAY).description("Comments"));

    fields =
        fields.andWithPrefix(
            "comments[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Comment ID"),
            fieldWithPath(".content")
                .type(JsonFieldType.STRING)
                .description("Comment content")
                .optional(),
            fieldWithPath(".deleted").type(JsonFieldType.BOOLEAN).description("Deleted flag"),
            fieldWithPath(".likeCount").type(JsonFieldType.NUMBER).description("Like count"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update timestamp"),
            fieldWithPath(".replies").type(JsonFieldType.ARRAY).description("Replies"));

    fields =
        fields.andWithPrefix(
            "comments[].author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author user ID"),
            fieldWithPath(".handle")
                .type(JsonFieldType.STRING)
                .description("Author handle")
                .optional());

    fields =
        fields.andWithPrefix(
            "comments[].replies[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Reply ID"),
            fieldWithPath(".content")
                .type(JsonFieldType.STRING)
                .description("Reply content")
                .optional(),
            fieldWithPath(".deleted").type(JsonFieldType.BOOLEAN).description("Deleted flag"),
            fieldWithPath(".likeCount").type(JsonFieldType.NUMBER).description("Like count"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update timestamp"),
            fieldWithPath(".replies").type(JsonFieldType.ARRAY).description("Nested replies"));

    fields =
        fields.andWithPrefix(
            "comments[].replies[].author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author user ID"),
            fieldWithPath(".handle")
                .type(JsonFieldType.STRING)
                .description("Author handle")
                .optional());

    return responseFields(fields.getFieldDescriptors());
  }
}
