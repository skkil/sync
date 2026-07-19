package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostRecommendationsResponseSnippets {

  public static GetPostRecommendationsResponse getGetPostRecommendationsResponse() {
    return new GetPostRecommendationsResponse(
        CursorPaginationResponseSnippets.of(List.of(PostSummarySnippets.getPostSummary())));
  }

  public static ResponseFieldsSnippet getGetPostRecommendationsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            PostSummarySnippets.getPostSummaryFields(".").toArray(FieldDescriptor[]::new));

    return responseFields(fields.getFieldDescriptors());
  }
}
