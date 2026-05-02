package com.skkil.sync.recruitment.dto.response;

import java.util.List;

public record GetJobApplicationFilesResponse(List<JobApplicationFile> files) {

  public static record JobApplicationFile(String url) {}
}
