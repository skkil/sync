package com.skkil.sync.reflection.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record GetReflectionActivitiesResponse(List<Activity> activities) {

  @Builder
  public static record Activity(LocalDate date, Long count) {}
}
