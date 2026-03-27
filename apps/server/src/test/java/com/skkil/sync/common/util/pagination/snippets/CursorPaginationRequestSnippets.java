package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import java.util.Map;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.util.MultiValueMap;

public class CursorPaginationRequestSnippets {

  private static final String DEFAULT_CURSOR = "cursor";
  private static final int DEFAULT_SIZE = 10;

  public static CursorPaginationRequest getCursorPaginationRequest() {
    return new CursorPaginationRequest(DEFAULT_CURSOR, DEFAULT_SIZE);
  }

  public static MultiValueMap<String, String> getCursorPaginationRequestQueryParams() {
    return MultiValueMap.fromSingleValue(
        Map.of("cursor", DEFAULT_CURSOR, "size", String.valueOf(DEFAULT_SIZE)));
  }

  public static QueryParametersSnippet getCursorPaginationRequestParameters() {
    return queryParameters(
        parameterWithName("cursor").description("Pagination Cursor").optional(),
        parameterWithName("size").description("Page Size (Long)"));
  }
}
