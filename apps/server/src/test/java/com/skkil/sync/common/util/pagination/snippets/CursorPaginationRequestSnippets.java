package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.util.MultiValueMap;

public class CursorPaginationRequestSnippets {

  private static final String DEFAULT_CURSOR = "cursor";
  private static final int DEFAULT_SIZE = 10;

  public static CursorPaginationRequest getCursorPaginationRequest() {
    return new CursorPaginationRequest(DEFAULT_SIZE, DEFAULT_CURSOR, null, null);
  }

  public static MultiValueMap<String, String> getCursorPaginationRequestQueryParams() {
    return MultiValueMap.fromSingleValue(
        new HashMap<>(Map.of("first", String.valueOf(DEFAULT_SIZE), "after", DEFAULT_CURSOR)));
  }

  public static QueryParametersSnippet getCursorPaginationRequestParameters() {
    return queryParameters(
        parameterWithName("first")
            .description("Number of items to return from the beginning of the list")
            .optional(),
        parameterWithName("after")
            .description(
                "Cursor for pagination, indicating the position after which to return items")
            .optional(),
        parameterWithName("last")
            .description("Number of items to return from the end of the list")
            .optional(),
        parameterWithName("before")
            .description(
                "Cursor for pagination, indicating the position before which to return items")
            .optional());
  }
}
