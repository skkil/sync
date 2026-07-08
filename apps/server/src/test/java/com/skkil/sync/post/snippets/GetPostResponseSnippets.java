package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetPostResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostResponseSnippets {

  public static GetPostResponse getGetPostResponse() {
    GetPostResponse.Media media =
        GetPostResponse.Media.builder().id(1L).url("https://example.com/media.png").build();

    GetPostResponse.Content content =
        GetPostResponse.Content.builder().json("Post Content").media(List.of(media)).build();

    return GetPostResponse.builder()
        .summary(PostSummarySnippets.getPostSummary())
        .content(content)
        .build();
  }

  public static ResponseFieldsSnippet getPostResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("summary").type(JsonFieldType.OBJECT).description("포스트 정보"));
    fields.addAll(PostSummarySnippets.getPostSummaryFields("summary."));
    fields.add(fieldWithPath("content").type(JsonFieldType.OBJECT).description("Post Content"));
    fields.add(
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("Post Content JSON"));
    fields.add(
        fieldWithPath("content.media")
            .type(JsonFieldType.ARRAY)
            .description("Media attached to the post"));
    fields.add(
        fieldWithPath("content.media[].id").type(JsonFieldType.NUMBER).description("Media ID"));
    fields.add(
        fieldWithPath("content.media[].url").type(JsonFieldType.STRING).description("Media URL"));
    return responseFields(fields.toArray(FieldDescriptor[]::new));
  }
}
