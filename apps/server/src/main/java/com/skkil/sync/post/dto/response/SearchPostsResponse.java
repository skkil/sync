package com.skkil.sync.post.dto.response;

import com.skkil.sync.post.dto.summary.PostSummary;
import java.util.List;

public record SearchPostsResponse(List<PostSummary> posts) {}
