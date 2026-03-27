package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.FieldDescriptors;
import org.springframework.restdocs.payload.JsonFieldType;

public class PaginationResponseSnippets {

  public static FieldDescriptors getPaginationResponseFields(String pathPrefix) {
    FieldDescriptors fields = new FieldDescriptors();

    return fields.andWithPrefix(
        pathPrefix,
        fieldWithPath(".content").type(JsonFieldType.ARRAY).description("Content"),
        fieldWithPath(".hasNext").type(JsonFieldType.BOOLEAN).description("Has Next"),
        fieldWithPath(".hasPrevious").type(JsonFieldType.BOOLEAN).description("Has Previous"));
  }
}
