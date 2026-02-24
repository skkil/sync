package com.skkil.sync.user.dto.response;

import java.util.List;

public record GetConnectionsResponse(List<Connection> connections) {

  public static record Connection(Long userId, String name) {}
}
