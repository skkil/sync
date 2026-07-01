package com.skkil.sync.recommendation.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse.Author;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse.FeedItem;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetFeedResponseSnippets {

  public static GetFeedResponse getGetFeedResponse() {
    Author author = new Author(1L, "user_handle", "User Name", "https://example.com/profile.jpg");

    FeedItem feedItem =
        new FeedItem(
            1L,
            "test-slug",
            author,
            "This is a feed item content",
            new GetFeedResponse.Project("project_handle", "name"),
            10L,
            5L,
            true,
            OffsetDateTime.of(2026, 5, 4, 12, 0, 0, 0, ZoneOffset.UTC));

    return new GetFeedResponse(CursorPaginationResponseSnippets.of(List.of(feedItem)));
  }

  public static ResponseFieldsSnippet getGetFeedResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("items");

    fields =
        fields.andWithPrefix(
            "items.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Feed Item ID"),
            fieldWithPath(".slug").type(JsonFieldType.STRING).description("Post Slug"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Content"),
            fieldWithPath(".project")
                .type(JsonFieldType.OBJECT)
                .description("Project Information")
                .optional(),
            fieldWithPath(".project.handle")
                .type(JsonFieldType.STRING)
                .description("Project Handle"),
            fieldWithPath(".project.name").type(JsonFieldType.STRING).description("Project Name"),
            fieldWithPath(".likeCount").type(JsonFieldType.NUMBER).description("Like Count"),
            fieldWithPath(".commentCount").type(JsonFieldType.NUMBER).description("Comment Count"),
            fieldWithPath(".bookmarked")
                .type(JsonFieldType.BOOLEAN)
                .description("Whether the current user bookmarked this feed item"),
            fieldWithPath(".createdAt").type(JsonFieldType.STRING).description("Created At"),
            fieldWithPath(".author").type(JsonFieldType.OBJECT).description("Author Information"));

    fields =
        fields.andWithPrefix(
            "items.nodes[].content.author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author ID"),
            fieldWithPath(".handle").type(JsonFieldType.STRING).description("Author Handle"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Author Name"),
            fieldWithPath(".profileImageUrl")
                .type(JsonFieldType.STRING)
                .description("Author Profile Image URL")
                .optional());

    return responseFields(fields.getFieldDescriptors());
  }
}
