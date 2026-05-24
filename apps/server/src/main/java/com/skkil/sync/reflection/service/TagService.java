package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.constants.ReflectionConstants;
import com.skkil.sync.reflection.dto.response.SearchTagsResponse;
import com.skkil.sync.reflection.exception.ReflectionTagLimitExceededException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.model.ReflectionTag;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.reflection.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

  private final TagRepository tagRepository;

  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public SearchTagsResponse searchTags(String query) {
    var tags =
        tagRepository.searchTags(query).stream()
            .map(
                tag ->
                    SearchTagsResponse.Tag.builder()
                        .name(tag.getName())
                        .description(tag.getDescription())
                        .postCount(tag.getPostCount())
                        .build())
            .toList();

    return new SearchTagsResponse(tags);
  }

  @Transactional
  public void addTagsToReflection(Reflection reflection, List<String> tags) {
    if (tags.size() > ReflectionConstants.MAX_TAGS_PER_REFLECTION) {
      throw new ReflectionTagLimitExceededException();
    }

    for (String name : tags) {
      Tag tag =
          tagRepository
              .findByName(name)
              .orElseGet(
                  () -> {
                    Tag newTag = Tag.builder().name(name).build();
                    return tagRepository.save(newTag);
                  });

      ReflectionTag reflectionTag = ReflectionTag.builder().reflection(reflection).tag(tag).build();
      reflection.addTag(reflectionTag);
      tagRepository.incrementPostCount(tag);
    }
  }
}
