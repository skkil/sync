package com.skkil.sync.post.dto.response;

import java.util.List;

public record SearchPostsResponse(List<GetPostsResponse.Post> posts) {}
