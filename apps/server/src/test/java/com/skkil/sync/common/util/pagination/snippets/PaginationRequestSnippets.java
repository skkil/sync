package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import java.util.Map;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.util.MultiValueMap;

public class PaginationRequestSnippets {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 10;

  public static PaginationRequest getPaginationRequest() {
    return new PaginationRequest(DEFAULT_PAGE, DEFAULT_SIZE);
  }

  public static MultiValueMap<String, String> getPaginationRequestQueryParams() {
    return MultiValueMap.fromSingleValue(
        Map.of(
            "page", String.valueOf(DEFAULT_PAGE),
            "size", String.valueOf(DEFAULT_SIZE)));
  }

  public static QueryParametersSnippet getPaginationRequestParameters() {
    return queryParameters(
        parameterWithName("page").description("Page (Integer)").optional(),
        parameterWithName("size").description("Page Size (Integer)"));
  }
}
