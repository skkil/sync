package com.skkil.sync.experience.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.experience.dto.request.UpdateReflectionRequest;
import com.skkil.sync.experience.service.ReflectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(ReflectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReflectionControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private ReflectionService reflectionService;

  @Test
  void updateReflection() throws Exception {
    UpdateReflectionRequest request = new UpdateReflectionRequest("updated");

    mockMvc
        .perform(
            patch("/experiences/{experienceId}/reflections/{reflectionId}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    verify(reflectionService).updateReflection(eq(1L), eq(2L), any(UpdateReflectionRequest.class));
  }

  @Test
  void deleteReflection() throws Exception {
    mockMvc
        .perform(delete("/experiences/{experienceId}/reflections/{reflectionId}", 1L, 2L))
        .andExpect(status().isNoContent());

    verify(reflectionService).deleteReflection(1L, 2L);
  }
}
