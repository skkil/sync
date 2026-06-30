package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.response.SearchTagsResponse;
import com.skkil.sync.post.service.TagService;
import com.skkil.sync.post.snippets.SearchTagsResponseSnippets;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class TagControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TagService tagService;

  @Test
  @DisplayName("[searchTags] API 문서화 테스트")
  void searchTags() throws Exception {
    String query = "java";
    var response = SearchTagsResponseSnippets.getSearchTagsResponse();

    when(tagService.searchTags(query)).thenReturn(response);

    mockMvc
        .perform(get("/search/tags").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchTags",
                ResourceSnippetParameters.builder()
                    .tag("tag")
                    .summary("Search Tags")
                    .description("검색어로 태그를 검색합니다.")
                    .responseSchema(schema(SearchTagsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("query").description("태그 검색어")),
                SearchTagsResponseSnippets.getSearchTagsResponseFields()));
  }
}
