package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateProjectRequestSnippets {

  public static UpdateProjectRequest getUpdateProjectRequest() {
    return new UpdateProjectRequest("프로젝트 설명입니다.", "https://example.com", null, null);
  }

  public static RequestFieldsSnippet getUpdateProjectRequestFields() {
    return requestFields(
        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("프로젝트 설명"),
        fieldWithPath("website").type(JsonFieldType.STRING).optional().description("프로젝트 웹사이트 URL"),
        fieldWithPath("iconMediaId")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 아이콘으로 설정할 미디어 ID"),
        fieldWithPath("removeIcon")
            .type(JsonFieldType.BOOLEAN)
            .optional()
            .description("프로젝트 아이콘 제거 여부"));
  }
}
