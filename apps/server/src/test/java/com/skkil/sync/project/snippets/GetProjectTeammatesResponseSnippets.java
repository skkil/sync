package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.model.Role;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectTeammatesResponseSnippets {

  public static GetProjectTeammatesResponse getGetProjectTeammatesResponse() {
    return new GetProjectTeammatesResponse(
        List.of(
            GetProjectTeammatesResponse.Teammate.builder()
                .handle("john")
                .name("John Doe")
                .role(Role.ADMIN)
                .profileImageUrl("https://example.com/john.png")
                .build(),
            GetProjectTeammatesResponse.Teammate.builder()
                .handle("jane")
                .name("Jane Doe")
                .role(Role.MEMBER)
                .profileImageUrl("https://example.com/jane.png")
                .build()));
  }

  public static ResponseFieldsSnippet getGetProjectTeammatesResponseFields() {
    return responseFields(
        fieldWithPath("teammates").type(JsonFieldType.ARRAY).description("팀원 목록"),
        fieldWithPath("teammates[].handle").type(JsonFieldType.STRING).description("팀원 핸들"),
        fieldWithPath("teammates[].name").type(JsonFieldType.STRING).description("팀원 이름"),
        fieldWithPath("teammates[].role")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("팀원 역할")
            .attributes(RestDocsUtils.getEnumAttributes(Role.class)),
        fieldWithPath("teammates[].profileImageUrl")
            .type(JsonFieldType.STRING)
            .description("팀원 프로필 이미지 URL")
            .optional());
  }
}
