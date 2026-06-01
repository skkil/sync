package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.constants.ReflectionConstants;
import com.skkil.sync.reflection.dto.response.SearchTagsResponse;
import com.skkil.sync.reflection.exception.ReflectionTagLimitExceededException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.model.ReflectionTag;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.reflection.repository.TagRepository;
import java.util.ArrayList;
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
  public List<Tag> addTagsToReflection(Reflection reflection, List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }

    List<String> filteredTags =
        tags.stream()
            .filter(tag -> tag != null)
            .map(String::trim)
            .filter(tag -> !tag.isBlank())
            .distinct()
            .toList();

    if (filteredTags.size() > ReflectionConstants.MAX_TAGS_PER_REFLECTION) {
      throw new ReflectionTagLimitExceededException();
    }

    List<Tag> reflectionTags = new ArrayList<>();
    for (String name : filteredTags) {
      Tag tag =
          tagRepository
              .findByName(name)
              .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));

      ReflectionTag reflectionTag = ReflectionTag.builder().reflection(reflection).tag(tag).build();
      reflection.addTag(reflectionTag);
      tagRepository.incrementPostCount(tag);
      reflectionTags.add(tag);
    }

    return reflectionTags;
  }

  @Transactional
  public void decrementPostCounts(List<Tag> tags) {
    for (Tag tag : tags) {
      tagRepository.decrementPostCount(tag);
    }
  }
}
