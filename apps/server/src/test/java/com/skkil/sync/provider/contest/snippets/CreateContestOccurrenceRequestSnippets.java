package com.skkil.sync.provider.contest.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.provider.contest.dto.request.CreateContestOccurrenceRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateContestOccurrenceRequestSnippets {

  public static CreateContestOccurrenceRequest getCreateContestOccurrenceRequest() {
    return new CreateContestOccurrenceRequest("Title", "Description");
  }

  public static RequestFieldsSnippet getCreateContestOccurrenceRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Contest Occurrence Title"),
        fieldWithPath("description")
            .type(JsonFieldType.STRING)
            .description("Contest Occurrence Description")
            .optional());
  }
}
