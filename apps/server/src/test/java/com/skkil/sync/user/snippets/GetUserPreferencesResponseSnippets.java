package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.user.dto.response.GetUserPreferencesResponse;
import com.skkil.sync.user.enums.Theme;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetUserPreferencesResponseSnippets {

  public static GetUserPreferencesResponse getGetUserPreferencesResponse() {
    return new GetUserPreferencesResponse(Theme.DARK);
  }

  public static ResponseFieldsSnippet getUserPreferencesResponseFields() {
    return responseFields(
        fieldWithPath("theme").type(JsonFieldType.STRING).description("User theme preference"));
  }
}
