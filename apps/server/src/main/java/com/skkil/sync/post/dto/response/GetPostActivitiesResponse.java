package com.skkil.sync.post.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record GetPostActivitiesResponse(List<Activity> activities) {

  @Builder
  public static record Activity(LocalDate date, Long count) {}
}
