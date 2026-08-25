package com.skkil.sync.post.dto.response;

import com.skkil.sync.post.dto.summary.PostTemplateSummary;
import java.util.List;

public record GetPostTemplatesResponse(List<PostTemplateSummary> templates) {}
