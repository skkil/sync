package com.skkil.sync.provider.contest.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.provider.contest.dto.response.GetContestOccurrenceResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetContestOccurrenceResponseSnippets {

  public static GetContestOccurrenceResponse getGetContestOccurrenceResponse() {
    GetContestOccurrenceResponse.Contest contest =
        new GetContestOccurrenceResponse.Contest(1L, "Contest Name");

    return new GetContestOccurrenceResponse(
        1L, contest, "Occurrence Title", "Occurrence Description");
  }

  public static ResponseFieldsSnippet getGetContestOccurrenceResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Contest Occurrence ID"),
        fieldWithPath("contest.id").type(JsonFieldType.NUMBER).description("Contest ID"),
        fieldWithPath("contest.name").type(JsonFieldType.STRING).description("Contest Name"),
        fieldWithPath("title").type(JsonFieldType.STRING).description("Occurrence Title"),
        fieldWithPath("description")
            .type(JsonFieldType.STRING)
            .description("Occurrence Description")
            .optional());
  }
}
