package com.skkil.sync.post.controller;

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
import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.service.PostSearchService;
import com.skkil.sync.post.snippets.SearchPostsResponseSnippets;
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

@WebMvcTest(PostSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostSearchControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostSearchService postSearchService;

  @Test
  @DisplayName("[searchPosts] API 문서화 테스트")
  void searchPosts() throws Exception {
    String query = "test query";
    SearchPostsResponse response = SearchPostsResponseSnippets.getSearchPostsResponse();

    when(postSearchService.searchPosts(eq(query))).thenReturn(response);

    mockMvc
        .perform(get("/search/posts").queryParam("query", query))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SearchPosts",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Search Posts")
                    .description("Search Posts by query string")
                    .responseSchema(schema("SearchPostsResponse")),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("query").description("Search query (1-100 characters)")),
                SearchPostsResponseSnippets.getSearchPostsResponseFields()));
  }
}
