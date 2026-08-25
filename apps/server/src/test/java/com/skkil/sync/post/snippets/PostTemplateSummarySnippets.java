package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.post.dto.summary.PostTemplateSummary;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class PostTemplateSummarySnippets {

  public static PostTemplateSummary getPostTemplateSummary() {
    return new PostTemplateSummary(
        "template-external-id",
        "트러블슈팅 리포트",
        "트러블슈팅: ",
        "{\"type\":\"doc\",\"content\":[{\"type\":\"heading\",\"attrs\":{\"level\":2},"
            + "\"content\":[{\"type\":\"text\",\"text\":\"증상\"}]},{\"type\":\"paragraph\"}]}",
        Instant.parse("2026-01-01T00:00:00Z"));
  }

  public static List<FieldDescriptor> getPostTemplateSummaryFields(String prefix) {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(
        fieldWithPath(prefix + "externalId").type(JsonFieldType.STRING).description("템플릿 외부 식별자"));
    fields.add(fieldWithPath(prefix + "name").type(JsonFieldType.STRING).description("템플릿 이름"));
    fields.add(
        fieldWithPath(prefix + "title")
            .type(JsonFieldType.STRING)
            .description("템플릿 적용 시 글 제목 칸에 채울 접두어 (없으면 제목을 건드리지 않음)")
            .optional());
    fields.add(
        fieldWithPath(prefix + "content")
            .type(JsonFieldType.STRING)
            .description("본문 스캐폴드 (Tiptap JSON 문자열)"));
    fields.add(
        fieldWithPath(prefix + "updatedAt").type(JsonFieldType.STRING).description("마지막 수정 시각"));
    return fields;
  }
}
