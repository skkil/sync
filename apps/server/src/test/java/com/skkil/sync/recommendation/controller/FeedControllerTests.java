package com.skkil.sync.recommendation.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationRequestSnippets;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse;
import com.skkil.sync.recommendation.service.FeedService;
import com.skkil.sync.recommendation.snippets.GetFeedResponseSnippets;
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

@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class FeedControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private FeedService feedService;

  @Test
  @DisplayName("[getRecentFeed] API 문서화 테스트")
  @WithAuthenticatedUser
  void getRecentFeed() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetFeedResponse response = GetFeedResponseSnippets.getGetFeedResponse();
    MultiValueMap<String, String> queryParams =
        CursorPaginationRequestSnippets.getCursorPaginationRequestQueryParams();

    when(feedService.getRecentFeed(eq(user.userId()), any())).thenReturn(response);

    mockMvc
        .perform(get("/feed/recent").queryParams(queryParams))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetRecentFeed",
                ResourceSnippetParameters.builder()
                    .tag("feed")
                    .summary("Get Recent Feed")
                    .description("Get Recent Feed")
                    .responseSchema(schema(GetFeedResponse.class.getSimpleName())),
                preprocessRequest(),
                preprocessResponse(prettyPrint()),
                Function.identity(),
                CursorPaginationRequestSnippets.getCursorPaginationRequestParameters(),
                GetFeedResponseSnippets.getGetFeedResponseFields()));
  }
}
