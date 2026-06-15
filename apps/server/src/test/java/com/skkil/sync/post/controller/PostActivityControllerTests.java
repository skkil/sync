package com.skkil.sync.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import com.skkil.sync.post.service.PostActivityService;
import com.skkil.sync.post.snippets.GetPostActivitiesResponseSnippets;
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

@WebMvcTest(PostActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class PostActivityControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PostActivityService postActivityService;

  @Test
  @DisplayName("[getPostActivities] API 문서화 테스트")
  void getPostActivities() throws Exception {
    String handle = "testuser";
    int year = 2026;

    GetPostActivitiesResponse response =
        GetPostActivitiesResponseSnippets.getGetPostActivitiesResponse();

    when(postActivityService.getPostActivities(handle, year)).thenReturn(response);

    mockMvc
        .perform(
            get("/profiles/{handle}/posts/activities", handle)
                .queryParam("year", String.valueOf(year)))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetPostActivities",
                ResourceSnippetParameters.builder()
                    .tag("post")
                    .summary("Get Post Activities")
                    .description("Get Post Activities")
                    .responseSchema(schema("GetPostActivitiesResponse")),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("User Handle")),
                queryParameters(parameterWithName("year").description("Year of Activities")),
                GetPostActivitiesResponseSnippets.getPostActivitiesResponseFields()));
  }
}
