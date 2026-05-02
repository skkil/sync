package com.skkil.sync.search.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.search.dto.response.SearchResponse;
import com.skkil.sync.search.enums.SearchType;
import com.skkil.sync.search.service.SearchService;
import com.skkil.sync.search.snippets.SearchResponseSnippets;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class SearchControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SearchService searchService;

  @Test
  @DisplayName("[search] API 문서화 테스트")
  void search() throws Exception {
    String query = "sync";
    SearchType type = SearchType.COMPANY;
    OffsetPaginationRequest pagination = OffsetPaginationRequestSnippets.getPaginationRequest();
    SearchResponse response = SearchResponseSnippets.getSearchResponse();

    MultiValueMap<String, String> queryParams =
        OffsetPaginationRequestSnippets.getPaginationRequestQueryParams();
    queryParams.add("query", query);
    queryParams.add("type", type.name());

    when(searchService.search(eq(query), eq(type), eq(pagination))).thenReturn(response);

    mockMvc
        .perform(get("/search").queryParams(queryParams))
        .andExpect(status().isOk())
        .andDo(
            document(
                "Search",
                ResourceSnippetParameters.builder()
                    .tag("search")
                    .summary("Search")
                    .description("Search")
                    .responseSchema(schema(SearchResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                OffsetPaginationRequestSnippets.getPaginationRequestParameters()
                    .and(
                        parameterWithName("query").description("Search Query"),
                        parameterWithName("type").description("Search Type")),
                SearchResponseSnippets.getSearchResponseFieldsSnippet()));
  }
}
