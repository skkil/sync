package com.skkil.sync.post.snippets;

import com.skkil.sync.post.dto.response.GetTagRecommendationsResponse;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTagRecommendationsResponseSnippets {

  public static GetTagRecommendationsResponse getGetTagRecommendationsResponse() {
    return new GetTagRecommendationsResponse(GetTagsResponseSnippets.getGetTagsResponse().tags());
  }

  public static ResponseFieldsSnippet getGetTagRecommendationsResponseFields() {
    return GetTagsResponseSnippets.getGetTagsResponseFields();
  }
}
