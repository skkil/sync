package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.post.dto.request.CreatePostTemplateRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreatePostTemplateRequestSnippets {

  public static CreatePostTemplateRequest getCreatePostTemplateRequest() {
    return new CreatePostTemplateRequest(
        "트러블슈팅 리포트",
        "트러블슈팅: ",
        "{\"type\":\"doc\",\"content\":[{\"type\":\"heading\",\"attrs\":{\"level\":2},"
            + "\"content\":[{\"type\":\"text\",\"text\":\"증상\"}]},{\"type\":\"paragraph\"}]}");
  }

  public static RequestFieldsSnippet getCreatePostTemplateRequestFields() {
    return requestFields(
        fieldWithPath("name")
            .type(JsonFieldType.STRING)
            .description("템플릿 이름 (프로젝트 안에서 유일, 최대 255자)"),
        fieldWithPath("title")
            .type(JsonFieldType.STRING)
            .description("템플릿 적용 시 글 제목 칸에 채울 접두어 (선택, 최대 255자)")
            .optional(),
        fieldWithPath("content")
            .type(JsonFieldType.STRING)
            .description("본문 스캐폴드 (Tiptap JSON 문자열, 최대 100,000자)"));
  }
}
