package com.skkil.sync.provider.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.constant.SchoolType;
import com.skkil.sync.provider.dto.request.CreateSchoolRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateSchoolRequest;
import com.skkil.sync.provider.dto.response.CreateProviderResponse;
import com.skkil.sync.provider.dto.response.GetSchoolResponse;
import com.skkil.sync.provider.service.ProviderService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(ProviderController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class ProviderControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProviderService providerService;

  @Autowired private JsonMapper jsonMapper;

  @Test
  void createSchool() throws Exception {
    CreateProviderResponse response = new CreateProviderResponse(1L);
    Mockito.when(providerService.createProvider(any(CreateSchoolRequest.class)))
        .thenReturn(response);

    CreateSchoolRequest request =
        CreateSchoolRequest.builder()
            .type(ProviderType.SCHOOL)
            .name("name")
            .description("description")
            .schoolType(SchoolType.UNIVERSITY)
            .build();

    mockMvc
        .perform(
            post("/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(
            document(
                "create-provider-school",
                requestFields(
                    fieldWithPath("type").type(JsonFieldType.STRING).description("Provider type"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Provider name"),
                    fieldWithPath("description")
                        .type(JsonFieldType.STRING)
                        .description("Provider description"),
                    fieldWithPath("schoolType")
                        .type(JsonFieldType.STRING)
                        .description("School type")),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Provider ID"))));
  }

  @Test
  void getSchool() throws Exception {
    GetSchoolResponse response =
        GetSchoolResponse.builder()
            .id(1L)
            .type(ProviderType.SCHOOL)
            .name("name")
            .description("description")
            .contactInfo("contact")
            .schoolType(SchoolType.HIGH_SCHOOL)
            .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
            .updatedAt(LocalDateTime.now(ZoneId.systemDefault()))
            .build();

    Mockito.when(providerService.getProvider(eq(1L))).thenReturn(response);

    mockMvc
        .perform(get("/providers/{id}", 1L))
        .andExpect(status().isOk())
        .andDo(
            document(
                "providers-get",
                pathParameters(parameterWithName("id").description("Provider ID")),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Provider id"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description("Provider type"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Provider name"),
                    fieldWithPath("description")
                        .type(JsonFieldType.STRING)
                        .description("Provider description"),
                    fieldWithPath("contactInfo")
                        .type(JsonFieldType.STRING)
                        .description("Provider contact info"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Created at"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("Updated at"),
                    fieldWithPath("schoolType")
                        .type(JsonFieldType.STRING)
                        .description("School type"))));
  }

  @Test
  void updateSchool() throws Exception {
    Mockito.doNothing()
        .when(providerService)
        .updateProvider(eq(1L), any(UpdateProviderRequest.class));

    UpdateSchoolRequest request =
        UpdateSchoolRequest.builder()
            .type(ProviderType.SCHOOL)
            .name("name")
            .description("description")
            .schoolType(SchoolType.UNIVERSITY)
            .build();

    mockMvc
        .perform(
            patch("/providers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "update-provider-school",
                pathParameters(parameterWithName("id").description("Provider ID")),
                requestFields(
                    fieldWithPath("type").type(JsonFieldType.STRING).description("Provider type"),
                    fieldWithPath("name")
                        .type(JsonFieldType.STRING)
                        .optional()
                        .description("Provider name"),
                    fieldWithPath("description")
                        .type(JsonFieldType.STRING)
                        .optional()
                        .description("Provider description"),
                    fieldWithPath("schoolType")
                        .type(JsonFieldType.STRING)
                        .optional()
                        .description("School type"))));
  }

  @Test
  void deleteProvider() throws Exception {
    Mockito.doNothing().when(providerService).deleteProvider(eq(1L));

    mockMvc
        .perform(delete("/providers/{id}", 1L))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "delete-provider",
                pathParameters(parameterWithName("id").description("Provider ID"))));
  }
}
