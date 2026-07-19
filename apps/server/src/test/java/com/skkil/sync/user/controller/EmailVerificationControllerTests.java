package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.dto.request.VerifyEmailRequest;
import com.skkil.sync.user.dto.response.SendVerificationEmailResponse;
import com.skkil.sync.user.service.EmailVerificationService;
import java.time.Instant;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(EmailVerificationController.class)
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class EmailVerificationControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private EmailVerificationService emailVerificationService;

  @Test
  @DisplayName("[sendVerificationEmail] API 문서화 테스트")
  @WithAuthenticatedUser(id = 1L)
  void sendVerificationEmail() throws Exception {
    SendVerificationEmailResponse response =
        new SendVerificationEmailResponse(Instant.parse("2026-07-16T00:10:00Z"), 600L);
    when(emailVerificationService.sendVerificationEmail(1L)).thenReturn(response);

    mockMvc
        .perform(
            post("/auth/email-verification/send")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(
            document(
                "SendVerificationEmail",
                ResourceSnippetParameters.builder()
                    .tag("auth")
                    .summary("Send Verification Email")
                    .description("로그인한 사용자에게 이메일 인증 코드를 발송합니다.")
                    .responseSchema(schema(SendVerificationEmailResponse.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                responseFields(
                    fieldWithPath("expiresAt")
                        .type(JsonFieldType.STRING)
                        .description("인증 코드가 만료되는 시각"),
                    fieldWithPath("validForSeconds")
                        .type(JsonFieldType.NUMBER)
                        .description("인증 코드의 유효 시간(초)"))));

    verify(emailVerificationService).sendVerificationEmail(eq(1L));
  }

  @Test
  @DisplayName("[sendVerificationEmail] 로그인하지 않은 사용자는 접근할 수 없다")
  void sendVerificationEmail_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            post("/auth/email-verification/send")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[verifyEmail] API 문서화 테스트")
  @WithAuthenticatedUser(id = 1L)
  void verifyEmail() throws Exception {
    VerifyEmailRequest request = new VerifyEmailRequest("ABCDEF");

    mockMvc
        .perform(
            post("/auth/email-verification/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "VerifyEmail",
                ResourceSnippetParameters.builder()
                    .tag("auth")
                    .summary("Verify Email")
                    .description("발송된 이메일 인증 코드를 확인하여 이메일 인증을 완료합니다.")
                    .requestSchema(schema(VerifyEmailRequest.class.getSimpleName())),
                preprocessRequest(modifyHeaders().set("Content-Type", "application/json")),
                null,
                Function.identity(),
                requestFields(
                    fieldWithPath("token")
                        .type(JsonFieldType.STRING)
                        .description("이메일로 발송된 인증 코드"))));

    verify(emailVerificationService).verifyEmail(eq(1L), eq(request));
  }

  @Test
  @DisplayName("[verifyEmail] 로그인하지 않은 사용자는 접근할 수 없다")
  void verifyEmail_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    VerifyEmailRequest request = new VerifyEmailRequest("ABCDEF");

    mockMvc
        .perform(
            post("/auth/email-verification/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[verifyEmail] 토큰 길이가 올바르지 않으면 400을 반환한다")
  @WithAuthenticatedUser(id = 1L)
  void verifyEmail_tokenLengthIsInvalid_returnBadRequest() throws Exception {
    VerifyEmailRequest request = new VerifyEmailRequest("SHORT");

    mockMvc
        .perform(
            post("/auth/email-verification/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
