package com.skkil.sync.common.util.pagination.snippets;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class OffsetPaginationRequestSnippets {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 10;

  public static OffsetPaginationRequest getPaginationRequest() {
    return new OffsetPaginationRequest(DEFAULT_PAGE, DEFAULT_SIZE);
  }

  public static MultiValueMap<String, String> getPaginationRequestQueryParams() {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("page", String.valueOf(DEFAULT_PAGE));
    queryParams.add("size", String.valueOf(DEFAULT_SIZE));

    return queryParams;
  }

  public static QueryParametersSnippet getPaginationRequestParameters() {
    return queryParameters(
        parameterWithName("page").description("Page (Integer)").optional(),
        parameterWithName("size").description("Page Size (Integer)"));
  }
}
