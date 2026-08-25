package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.post.dto.request.UpdatePostTemplateRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdatePostTemplateRequestSnippets {

  public static UpdatePostTemplateRequest getUpdatePostTemplateRequest() {
    return new UpdatePostTemplateRequest(
        "주간 회고",
        "주간 회고: ",
        "{\"type\":\"doc\",\"content\":[{\"type\":\"heading\",\"attrs\":{\"level\":2},"
            + "\"content\":[{\"type\":\"text\",\"text\":\"잘한 것\"}]},{\"type\":\"paragraph\"}]}");
  }

  public static RequestFieldsSnippet getUpdatePostTemplateRequestFields() {
    return requestFields(
        fieldWithPath("name")
            .type(JsonFieldType.STRING)
            .description("템플릿 이름 (프로젝트 안에서 유일, 최대 255자)"),
        fieldWithPath("title")
            .type(JsonFieldType.STRING)
            .description("템플릿 적용 시 글 제목 칸에 채울 접두어 (선택, 최대 255자 — null 이면 접두어를 비움)")
            .optional(),
        fieldWithPath("content")
            .type(JsonFieldType.STRING)
            .description("본문 스캐폴드 (Tiptap JSON 문자열, 최대 100,000자)"));
  }
}
