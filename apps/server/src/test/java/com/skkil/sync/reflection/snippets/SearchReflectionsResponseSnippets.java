package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.SearchReflectionsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchReflectionsResponseSnippets {

  public static SearchReflectionsResponse getSearchReflectionsResponse() {
    SearchReflectionsResponse.Reflection reflection =
        SearchReflectionsResponse.Reflection.builder().id(1L).content("Reflection Content").build();

    return new SearchReflectionsResponse(List.of(reflection));
  }

  public static ResponseFieldsSnippet getSearchReflectionsResponseFields() {
    return responseFields(
        fieldWithPath("reflections").type(JsonFieldType.ARRAY).description("Reflection List"),
        fieldWithPath("reflections[].id").type(JsonFieldType.NUMBER).description("Reflection ID"),
        fieldWithPath("reflections[].content")
            .type(JsonFieldType.STRING)
            .description("Reflection Content"));
  }
}
