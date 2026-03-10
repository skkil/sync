package com.skkil.sync.provider.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.provider.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.service.company.JobPostingService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class CompanyControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private JobPostingService jobPostingService;

  @Autowired private JsonMapper jsonMapper;

  @Test
  void createJobPosting() throws Exception {
    CreateJobPostingRequest request =
        new CreateJobPostingRequest("Job Title", "Job Description", "Location");

    CreateJobPostingResponse response = new CreateJobPostingResponse("1");
    Mockito.when(jobPostingService.createJobPosting(any(), any())).thenReturn(response);

    mockMvc
        .perform(
            post("/companies/{companyId}/jobs", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isCreated());
  }

  @Test
  void getJobPostings() throws Exception {
    GetJobPostingsResponse response =
        new GetJobPostingsResponse(
            List.of(
                new GetJobPostingsResponse.JobPosting(
                    "1",
                    "",
                    "",
                    "",
                    LocalDateTime.now(ZoneId.systemDefault()),
                    LocalDateTime.now(ZoneId.systemDefault()))));
    Mockito.when(jobPostingService.getJobPostings(any())).thenReturn(response);

    mockMvc.perform(get("/companies/{companyId}/jobs", 1L)).andExpect(status().isOk());
  }
}
