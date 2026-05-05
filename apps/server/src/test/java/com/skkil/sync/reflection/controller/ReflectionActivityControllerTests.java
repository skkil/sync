package com.skkil.sync.reflection.controller;

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
import com.skkil.sync.reflection.dto.response.GetReflectionActivitiesResponse;
import com.skkil.sync.reflection.service.ReflectionActivityService;
import com.skkil.sync.reflection.snippets.GetReflectionActivitiesResponseSnippets;
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

@WebMvcTest(ReflectionActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ReflectionActivityControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReflectionActivityService reflectionActivityService;

  @Test
  @DisplayName("[getReflectionActivities] API 문서화 테스트")
  void getReflectionActivities() throws Exception {
    String handle = "testuser";
    int year = 2026;

    GetReflectionActivitiesResponse response =
        GetReflectionActivitiesResponseSnippets.getGetReflectionActivitiesResponse();

    when(reflectionActivityService.getReflectionActivities(handle, year)).thenReturn(response);

    mockMvc
        .perform(
            get("/profiles/{handle}/reflections/activities", handle)
                .queryParam("year", String.valueOf(year)))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetReflectionActivities",
                ResourceSnippetParameters.builder()
                    .tag("reflection")
                    .summary("Get Reflection Activities")
                    .description("Get Reflection Activities")
                    .responseSchema(schema("GetReflectionActivitiesResponse")),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("User Handle")),
                queryParameters(parameterWithName("year").description("Year of Activities")),
                GetReflectionActivitiesResponseSnippets.getReflectionActivitiesResponseFields()));
  }
}
