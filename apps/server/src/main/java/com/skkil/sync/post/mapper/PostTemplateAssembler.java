package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.GetPostTemplatesResponse;
import com.skkil.sync.post.dto.summary.PostTemplateSummary;
import com.skkil.sync.post.model.PostTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostTemplateAssembler {

  public PostTemplateSummary toSummary(PostTemplate template) {
    return new PostTemplateSummary(
        template.getExternalId(),
        template.getName(),
        template.getTitle(),
        template.getContent(),
        template.getUpdatedAt());
  }

  public GetPostTemplatesResponse toGetTemplatesResponse(List<PostTemplate> templates) {
    return new GetPostTemplatesResponse(templates.stream().map(this::toSummary).toList());
  }
}
