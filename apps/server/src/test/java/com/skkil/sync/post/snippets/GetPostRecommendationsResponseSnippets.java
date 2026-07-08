package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostRecommendationsResponseSnippets {

  public static GetPostRecommendationsResponse getGetPostRecommendationsResponse() {
    GetPostsResponse.Post post =
        GetPostsResponse.Post.builder()
            .summary(PostSummarySnippets.getPostSummary())
            .content("Post Content")
            .build();

    return new GetPostRecommendationsResponse(CursorPaginationResponseSnippets.of(List.of(post)));
  }

  public static ResponseFieldsSnippet getGetPostRecommendationsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            fieldWithPath(".summary").type(JsonFieldType.OBJECT).description("Post Summary"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Post Content"));

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content.summary",
            PostSummarySnippets.getPostSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
