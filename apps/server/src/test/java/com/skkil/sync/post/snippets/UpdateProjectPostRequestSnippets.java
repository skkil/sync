package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.post.dto.request.PostContentRequest;
import com.skkil.sync.post.dto.request.UpdateProjectPostRequest;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateProjectPostRequestSnippets {

  public static UpdateProjectPostRequest getUpdateProjectPostRequest() {
    PostContentRequest content =
        new PostContentRequest(
            "This is a post content.", "{\"text\": \"This is a post content.\"}", List.of(1L));

    return new UpdateProjectPostRequest(
        "Updated post title",
        PostType.LONG,
        PostStatus.PUBLISHED,
        content,
        List.of("java"),
        List.of("roadmap"));
  }

  public static RequestFieldsSnippet getUpdateProjectPostRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Title").optional(),
        fieldWithPath("type")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Type")
            .attributes(RestDocsUtils.getEnumAttributes(PostType.class)),
        fieldWithPath("status")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Status")
            .attributes(RestDocsUtils.getEnumAttributes(PostStatus.class)),
        fieldWithPath("content").type(JsonFieldType.OBJECT).description("Content"),
        fieldWithPath("content.text").type(JsonFieldType.STRING).description("Text Content"),
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("JSON Content"),
        fieldWithPath("content.mediaIds")
            .type(JsonFieldType.ARRAY)
            .description("사용된 미디어 ID 목록")
            .optional(),
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("전역 태그 목록").optional(),
        fieldWithPath("projectTags")
            .type(JsonFieldType.ARRAY)
            .description("프로젝트 태그 목록")
            .optional());
  }
}
