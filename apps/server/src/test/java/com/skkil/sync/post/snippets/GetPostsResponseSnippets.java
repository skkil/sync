package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.model.PostStatus;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostsResponseSnippets {

  public static GetPostsResponse getGetPostsResponse() {
    return getGetPostsResponse(PostStatus.PUBLISHED);
  }

  public static GetPostsResponse getGetDraftPostsResponse() {
    return getGetPostsResponse(PostStatus.DRAFT);
  }

  private static GetPostsResponse getGetPostsResponse(PostStatus status) {
    return new GetPostsResponse(
        CursorPaginationResponseSnippets.of(List.of(PostSummarySnippets.getPostSummary(status))));
  }

  public static ResponseFieldsSnippet getPostsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            PostSummarySnippets.getPostSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }

  public static GetPostsResponse getGetBookmarkedPostsResponse() {
    return new GetPostsResponse(
        CursorPaginationResponseSnippets.of(List.of(PostSummarySnippets.getPostSummary())));
  }

  public static ResponseFieldsSnippet getBookmarkedPostsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            PostSummarySnippets.getPostSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
