package com.skkil.sync.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.Role;
import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class AuthControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private AuthService authService;

  @Test
  void login() throws Exception {
    LoginRequest request = new LoginRequest("user@example.com", "password123");

    AuthenticatedUser user =
        new AuthenticatedUser(1L, "user", "user@example.com", "hashedPassword", Role.USER);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, request.password(), user.getAuthorities());

    when(authService.authenticate(any(LoginRequest.class))).thenReturn(authentication);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(
            document(
                "auth-login",
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("Email"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("Password"))));
  }

  @Test
  void register() throws Exception {
    RegisterRequest request = new RegisterRequest("newuser@example.com", "password123");

    doNothing().when(authService).registerUser(any(RegisterRequest.class));

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "auth-register",
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("Email"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("Password"))));
  }

  @Test
  void register_passwordIsBad_returnBadRequest() throws Exception {
    RegisterRequest request = new RegisterRequest("user@example.com", "short");

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
