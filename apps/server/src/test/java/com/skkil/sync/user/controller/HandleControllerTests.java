package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.dto.response.GetHandleAvailabilityResponse;
import com.skkil.sync.user.service.UserService;
import com.skkil.sync.user.snippets.GetHandleAvailabilityResponseSnippets;
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

@WebMvcTest(HandleController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class HandleControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @Test
  @DisplayName("[getHandleAvailability] API 문서화 테스트")
  @WithAuthenticatedUser
  void getHandleAvailability() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    String handle = "my-handle";

    GetHandleAvailabilityResponse response =
        GetHandleAvailabilityResponseSnippets.getGetHandleAvailabilityResponse();

    when(userService.getHandleAvailability(user.userId(), handle)).thenReturn(response);

    mockMvc
        .perform(get("/handles/availability").queryParam("handle", handle))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetHandleAvailability",
                ResourceSnippetParameters.builder()
                    .tag("user")
                    .summary("Get Handle Availability")
                    .description("사용자 핸들의 사용 가능 여부를 확인합니다.")
                    .responseSchema(schema(GetHandleAvailabilityResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(parameterWithName("handle").description("확인할 핸들")),
                GetHandleAvailabilityResponseSnippets.getResponseFields()));
  }

  @Test
  @DisplayName("[getHandleAvailability] 로그인하지 않은 사용자는 접근할 수 없다")
  void getHandleAvailability_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(get("/handles/availability").queryParam("handle", "my-handle"))
        .andExpect(status().isUnauthorized());
  }
}
