package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import java.time.Instant;
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

    GetCommentsResponse.Comment reply =
        new GetCommentsResponse.Comment(
            2L,
            author,
            "Reply content",
            false,
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-01-01T00:00:00Z"),
            List.of());
    GetCommentsResponse.Comment comment =
        new GetCommentsResponse.Comment(
            1L,
            author,
            "Comment content",
            false,
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
            fieldWithPath(".isDeleted").type(JsonFieldType.BOOLEAN).description("Is deleted"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update timestamp"),
            // TODO: Spring REST Docs does not support recursive field descriptors, need to
            // implement custom handling for replies
            subsectionWithPath(".replies").type(JsonFieldType.ARRAY).description("Replies"));

    fields =
        fields.andWithPrefix(
            "comments[].author",
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
