package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectResponseSnippets {

  public static GetProjectResponse getGetProjectResponse() {
    return new GetProjectResponse(
        "my-project",
        "나의 프로젝트",
        List.of(
            new GetProjectResponse.Teammate("1", true),
            new GetProjectResponse.Teammate("2", false)));
  }

  public static ResponseFieldsSnippet getGetProjectResponseFields() {
    return responseFields(
        fieldWithPath("handle").type(JsonFieldType.STRING).description("프로젝트 핸들"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("프로젝트 이름"),
        fieldWithPath("teammates").type(JsonFieldType.ARRAY).description("팀원 목록"),
        fieldWithPath("teammates[].id").type(JsonFieldType.STRING).description("팀원 ID"),
        fieldWithPath("teammates[].isOwner").type(JsonFieldType.BOOLEAN).description("오너 여부"));
  }
}
