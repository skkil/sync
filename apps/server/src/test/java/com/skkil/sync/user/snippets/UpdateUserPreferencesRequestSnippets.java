package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.user.dto.request.UpdateUserPreferencesRequest;
import com.skkil.sync.user.enums.Theme;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateUserPreferencesRequestSnippets {

  public static UpdateUserPreferencesRequest getUpdateUserPreferencesRequest() {
    return new UpdateUserPreferencesRequest(Theme.DARK);
  }

  public static RequestFieldsSnippet getUpdateUserPreferencesRequestFields() {
    return requestFields(
        fieldWithPath("theme")
            .type(JsonFieldType.STRING)
            .description("User theme preference")
            .optional());
  }
}
