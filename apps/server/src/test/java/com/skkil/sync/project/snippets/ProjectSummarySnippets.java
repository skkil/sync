package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.project.dto.summary.ProjectSummary;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ProjectSummarySnippets {

  public static ProjectSummary getProjectSummary() {
    return ProjectSummary.builder()
        .handle("my-project")
        .name("나의 프로젝트")
        .description("프로젝트 설명")
        .website("https://example.com")
        .isPublic(true)
        .iconUrl("https://example.com/icon.png")
        .build();
  }

  public static List<FieldDescriptor> getProjectSummaryFields(String prefix) {
    return List.of(
        fieldWithPath(prefix + "handle").type(JsonFieldType.STRING).description("프로젝트 핸들"),
        fieldWithPath(prefix + "name").type(JsonFieldType.STRING).description("프로젝트 이름"),
        fieldWithPath(prefix + "description")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 설명"),
        fieldWithPath(prefix + "website")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 웹사이트"),
        fieldWithPath(prefix + "isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),
        fieldWithPath(prefix + "iconUrl")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 아이콘 URL"));
  }
}
