package com.skkil.sync.user.dto.response;

import java.util.List;

public record GetConnectionsResponse(List<Connection> connections) {

  public static record Connection(
      String userId, String name, Provider provider, String profession) {}

  public static record Provider(String id, String type, String name) {}
}
