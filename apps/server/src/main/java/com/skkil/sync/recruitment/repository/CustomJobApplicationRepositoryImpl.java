package com.skkil.sync.recruitment.repository;

import static com.skkil.sync.jooq.tables.Companies.COMPANIES;
import static com.skkil.sync.jooq.tables.JobApplications.JOB_APPLICATIONS;
import static com.skkil.sync.jooq.tables.JobPostings.JOB_POSTINGS;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.recruitment.dto.data.JobApplicationDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
class CustomJobApplicationRepositoryImpl implements CustomJobApplicationRepository {

  private final DSLContext dsl;

  public CustomJobApplicationRepositoryImpl(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public JobApplicationDto findApplicationById(Long id) {
    return dsl.select(
            JOB_APPLICATIONS.ID,
            COMPANIES.ID.as("companyId"),
            PROVIDERS.NAME.as("companyName"),
            JOB_POSTINGS.JOB_TITLE.as("jobTitle"),
            JOB_POSTINGS.JOB_DESCRIPTION.as("jobDescription"),
            JOB_POSTINGS.LOCATION.as("jobLocation"),
            JOB_APPLICATIONS.STATUS.as("status"),
            JOB_APPLICATIONS.NOTES.as("notes"))
        .from(JOB_APPLICATIONS)
        .join(JOB_POSTINGS)
        .on(JOB_APPLICATIONS.JOB_POSTING_ID.eq(JOB_POSTINGS.ID))
        .join(PROVIDERS)
        .on(JOB_POSTINGS.COMPANY_ID.eq(PROVIDERS.ID))
        .join(COMPANIES)
        .on(JOB_POSTINGS.COMPANY_ID.eq(COMPANIES.ID))
        .where(JOB_APPLICATIONS.ID.eq(id))
        .fetchOneInto(JobApplicationDto.class);
  }
}
