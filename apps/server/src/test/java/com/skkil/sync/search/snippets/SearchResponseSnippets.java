package com.skkil.sync.search.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationResponseSnippets;
import com.skkil.sync.search.dto.response.SearchResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchResponseSnippets {

  public static SearchResponse getSearchResponse() {
    SearchResponse.Result result = SearchResponse.Result.builder().id(1L).name("Sync").build();
    SearchResponse.Count count =
        SearchResponse.Count.builder()
            .userCount(5L)
            .schoolCount(4L)
            .companyCount(3L)
            .contestCount(2L)
            .projectCount(1L)
            .build();

    return new SearchResponse(OffsetPaginationResponseSnippets.of(List.of(result)), count);
  }

  public static ResponseFieldsSnippet getSearchResponseFieldsSnippet() {
    FieldDescriptors fields =
        OffsetPaginationResponseSnippets.getPaginationResponseFields("results");

    fields =
        fields.andWithPrefix(
            "results.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Result ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Result Name"));

    fields =
        fields.and(
            fieldWithPath("count").type(JsonFieldType.OBJECT).description("Search Result Counts"),
            fieldWithPath("count.userCount").type(JsonFieldType.NUMBER).description("User Count"),
            fieldWithPath("count.schoolCount")
                .type(JsonFieldType.NUMBER)
                .description("School Count"),
            fieldWithPath("count.companyCount")
                .type(JsonFieldType.NUMBER)
                .description("Company Count"),
            fieldWithPath("count.contestCount")
                .type(JsonFieldType.NUMBER)
                .description("Contest Count"),
            fieldWithPath("count.projectCount")
                .type(JsonFieldType.NUMBER)
                .description("Project Count"));

    return responseFields(fields.getFieldDescriptors());
  }
}
