package com.skkil.sync.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProfileControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfileService profileService;

  @Test
  void getProfile() throws Exception {
    GetProfileResponse response =
        GetProfileResponse.builder()
            .userId("1")
            .name("name")
            .email("email@example.com")
            .bio("bio")
            .build();

    when(profileService.getProfile(eq(1L))).thenReturn(response);

    mockMvc
        .perform(get("/profiles/{userId}", 1L))
        .andExpect(status().isOk())
        .andDo(
            document(
                "profiles-get",
                pathParameters(parameterWithName("userId").description("User ID")),
                responseFields(
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("User ID"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("User name"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("User email"),
                    fieldWithPath("bio").type(JsonFieldType.STRING).description("User bio"))));
  }
}
