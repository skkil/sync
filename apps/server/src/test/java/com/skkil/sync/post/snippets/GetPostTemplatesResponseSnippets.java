package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetPostTemplatesResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostTemplatesResponseSnippets {

  public static GetPostTemplatesResponse getGetPostTemplatesResponse() {
    return new GetPostTemplatesResponse(
        List.of(PostTemplateSummarySnippets.getPostTemplateSummary()));
  }

  public static ResponseFieldsSnippet getGetPostTemplatesResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("templates").type(JsonFieldType.ARRAY).description("템플릿 목록 (이름순)"));
    fields.addAll(PostTemplateSummarySnippets.getPostTemplateSummaryFields("templates[]."));
    return responseFields(fields);
  }
}
