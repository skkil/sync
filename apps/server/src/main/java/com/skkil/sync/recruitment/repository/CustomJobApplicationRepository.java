package com.skkil.sync.recruitment.repository;

import com.skkil.sync.recruitment.dto.data.JobApplicationDto;

interface CustomJobApplicationRepository {

  JobApplicationDto findApplicationById(Long id);
}
