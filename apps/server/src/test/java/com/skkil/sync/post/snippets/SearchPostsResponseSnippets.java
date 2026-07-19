package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.SearchPostsResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchPostsResponseSnippets {

  public static SearchPostsResponse getSearchPostsResponse() {
    return new SearchPostsResponse(List.of(PostSummarySnippets.getPostSummary()));
  }

  public static ResponseFieldsSnippet getSearchPostsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("posts").type(JsonFieldType.ARRAY).description("Post List"));
    fields.addAll(PostSummarySnippets.getPostSummaryFields("posts[]."));

    return responseFields(fields.toArray(new FieldDescriptor[0]));
  }
}
