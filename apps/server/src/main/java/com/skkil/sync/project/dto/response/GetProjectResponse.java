package com.skkil.sync.project.dto.response;

import java.util.List;

public record GetProjectResponse(String handle, String name, List<Teammate> teammates) {

  public record Teammate(String id, boolean isOwner) {}
}
