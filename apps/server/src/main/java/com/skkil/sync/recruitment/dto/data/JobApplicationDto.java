package com.skkil.sync.recruitment.dto.data;

import com.skkil.sync.recruitment.enums.JobApplicationStatus;

public record JobApplicationDto(
    Long id,
    Long companyId,
    String companyName,
    String jobTitle,
    String jobDescription,
    String jobLocation,
    JobApplicationStatus status,
    String notes) {}
