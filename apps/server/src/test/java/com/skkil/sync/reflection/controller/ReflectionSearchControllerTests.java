package com.skkil.sync.reflection.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.reflection.dto.response.SearchReflectionsResponse;
import com.skkil.sync.reflection.service.ReflectionSearchService;
import com.skkil.sync.reflection.snippets.SearchReflectionsResponseSnippets;
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

@WebMvcTest(ReflectionSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionSearchControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReflectionSearchService reflectionSearchService;

  @Test
  @DisplayName("[searchReflections] API 문서화 테스트")
  void searchReflections() throws Exception {
    String query = "test query";
    SearchReflectionsResponse response =
        SearchReflectionsResponseSnippets.getSearchReflectionsResponse();

    when(reflectionSearchService.searchReflections(eq(query))).thenReturn(response);

    mockMvc
        .perform(get("/search/reflections").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchReflections",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Search Reflections")
                    .description("Search Reflections by query string")
                    .responseSchema(schema("SearchReflectionsResponse")),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("query").description("Search query (1-100 characters)")),
                SearchReflectionsResponseSnippets.getSearchReflectionsResponseFields()));
  }
}
