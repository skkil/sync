package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.PostContentRequest;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreatePostRequestSnippets {

  public static CreatePostRequest getCreatePostRequest() {
    PostContentRequest content =
        new PostContentRequest(
            "This is a post content.", "{\"text\": \"This is a post content.\"}", List.of(1L, 2L));

    return CreatePostRequest.builder()
        .title("title")
        .type(PostType.SHORT)
        .status(PostStatus.PUBLISHED)
        .content(content)
        .tags(List.of("java", "spring"))
        .build();
  }

  public static RequestFieldsSnippet getCreatePostRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Title").optional(),
        fieldWithPath("type")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Type")
            .attributes(RestDocsUtils.getEnumAttributes(PostType.class)),
        fieldWithPath("status")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Status")
            .attributes(RestDocsUtils.getEnumAttributes(PostStatus.class))
            .optional(),
        fieldWithPath("content").type(JsonFieldType.OBJECT).description("Content"),
        fieldWithPath("content.text").type(JsonFieldType.STRING).description("Text Content"),
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("JSON Content"),
        fieldWithPath("content.mediaIds")
            .type(JsonFieldType.ARRAY)
            .description("사용된 미디어 ID 목록")
            .optional(),
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록").optional());
  }
}
