package com.skkil.sync.provider.contest.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.dto.response.PaginationResponse;
import com.skkil.sync.common.util.pagination.snippets.PaginationResponseSnippets;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrencesResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetContestOccurrencesResponseSnippets {

  public static GetContestOccurrencesResponse getGetContestOccurrencesResponse() {
    GetContestOccurrencesResponse.Contest contest =
        new GetContestOccurrencesResponse.Contest(1L, "Contest Name");

    GetContestOccurrencesResponse.ContestOccurrence occurrence =
        new GetContestOccurrencesResponse.ContestOccurrence(
            1L, contest, "Occurrence Title", "Occurrence Description");

    return new GetContestOccurrencesResponse(
        PaginationResponse.<GetContestOccurrencesResponse.ContestOccurrence>builder()
            .content(List.of(occurrence))
            .hasNext(true)
            .hasPrevious(false)
            .build());
  }

  public static ResponseFieldsSnippet getGetContestOccurrencesResponseFields() {
    FieldDescriptors fields = PaginationResponseSnippets.getPaginationResponseFields("occurrences");

    fields =
        fields.andWithPrefix(
            "occurrences.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Contest Occurrence ID"),
            fieldWithPath(".title").type(JsonFieldType.STRING).description("Occurrence Title"),
            fieldWithPath(".description")
                .type(JsonFieldType.STRING)
                .description("Occurrence Description")
                .optional());

    fields =
        fields.andWithPrefix(
            "occurrences.content[].contest",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Contest ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Contest Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
