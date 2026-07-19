package com.skkil.sync.post.dto.response;

import com.skkil.sync.post.dto.summary.TagSummary;
import java.util.List;

public record GetTagsResponse(List<TagSummary> tags) {}
