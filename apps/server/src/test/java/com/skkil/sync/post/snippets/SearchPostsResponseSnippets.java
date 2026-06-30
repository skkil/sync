package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.SearchPostsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchPostsResponseSnippets {

  public static SearchPostsResponse getSearchPostsResponse() {
    SearchPostsResponse.Post post =
        SearchPostsResponse.Post.builder().id(1L).content("Post Content").build();

    return new SearchPostsResponse(List.of(post));
  }

  public static ResponseFieldsSnippet getSearchPostsResponseFields() {
    return responseFields(
        fieldWithPath("posts").type(JsonFieldType.ARRAY).description("Post List"),
        fieldWithPath("posts[].id").type(JsonFieldType.NUMBER).description("Post ID"),
        fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("Post Content"));
  }
}
