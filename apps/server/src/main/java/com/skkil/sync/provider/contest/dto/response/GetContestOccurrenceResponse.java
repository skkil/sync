package com.skkil.sync.provider.contest.dto.response;

public record GetContestOccurrenceResponse(
    Long id, Contest contest, String title, String description) {

  public static record Contest(Long id, String name) {}
}
