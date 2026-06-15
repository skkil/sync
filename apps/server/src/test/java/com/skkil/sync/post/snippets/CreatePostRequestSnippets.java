package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.model.PostType;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreatePostRequestSnippets {

  public static CreatePostRequest getCreatePostRequest() {
    CreatePostRequest.Content content =
        new CreatePostRequest.Content(
            "This is a post content.", "{\"text\": \"This is a post content.\"}");

    return CreatePostRequest.builder()
        .title("title")
        .type(PostType.SHORT)
        .content(content)
        .tags(List.of("java", "spring"))
        .projectId(1L)
        .build();
  }

  public static RequestFieldsSnippet getCreatePostRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Title").optional(),
        fieldWithPath("type")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Type")
            .attributes(RestDocsUtils.getEnumAttributes(PostType.class)),
        fieldWithPath("content").type(JsonFieldType.OBJECT).description("Content"),
        fieldWithPath("content.text").type(JsonFieldType.STRING).description("Text Content"),
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("JSON Content"),
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록").optional(),
        fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").optional());
  }
}
