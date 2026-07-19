package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.dto.summary.CommentSummary;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetCommentsResponseSnippets {

  public static GetCommentsResponse getGetCommentsResponse() {
    CommentSummary comment =
        CommentSummary.builder()
            .id(1L)
            .author(UserSummarySnippets.getUserSummary())
            .isPostAuthor(false)
            .content("Comment content")
            .isDeleted(false)
            .isAccepted(false)
            .createdAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
            .updatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
            .build();

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
            fieldWithPath(".author").type(JsonFieldType.OBJECT).description("Comment author"),
            fieldWithPath(".isPostAuthor")
                .type(JsonFieldType.BOOLEAN)
                .description("Whether the author is the post author"),
            fieldWithPath(".content")
                .type(JsonFieldType.STRING)
                .description("Comment content")
                .optional(),
            fieldWithPath(".isDeleted").type(JsonFieldType.BOOLEAN).description("Is deleted"),
            fieldWithPath(".isAccepted").type(JsonFieldType.BOOLEAN).description("Is accepted"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("Update timestamp"));

    fields =
        fields.andWithPrefix(
            "comments.nodes[].content.author",
            UserSummarySnippets.getUserSummaryFields(".")
                .toArray(org.springframework.restdocs.payload.FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
