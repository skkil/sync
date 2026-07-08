package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.model.Role;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateTeammateRequestSnippets {

  public static UpdateTeammateRequest getUpdateTeammateRequest() {
    return new UpdateTeammateRequest(Role.ADMIN);
  }

  public static RequestFieldsSnippet getUpdateTeammateRequestFields() {
    return requestFields(
        fieldWithPath("role")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("변경할 팀원 역할")
            .attributes(RestDocsUtils.getEnumAttributes(Role.class)));
  }
}
