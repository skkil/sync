package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.constants.ReflectionConstants;
import com.skkil.sync.reflection.dto.request.UpdateTagRequest;
import com.skkil.sync.reflection.dto.response.GetTagsResponse;
import com.skkil.sync.reflection.dto.response.SearchTagsResponse;
import com.skkil.sync.reflection.exception.ReflectionTagLimitExceededException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.model.ReflectionTag;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.reflection.repository.TagRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    if (tags == null || tags.isEmpty()) {
      return;
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

    for (String name : filteredTags) {
      Tag tag =
          tagRepository
              .findByName(name)
              .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));

      ReflectionTag reflectionTag = ReflectionTag.builder().reflection(reflection).tag(tag).build();
      reflection.addTag(reflectionTag);
      tagRepository.incrementPostCount(tag);
    }
  }

  @Transactional(readOnly = true)
  public GetTagsResponse getAllTags() {
    var tags =
        tagRepository.findAllOrderByCreatedAtDesc().stream()
            .map(
                tag ->
                    GetTagsResponse.Tag.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .description(tag.getDescription())
                        .postCount(tag.getPostCount())
                        .verified(tag.isVerified())
                        .createdAt(
                            LocalDateTime.ofInstant(tag.getCreatedAt(), ZoneId.systemDefault()))
                        .updatedAt(
                            LocalDateTime.ofInstant(tag.getUpdatedAt(), ZoneId.systemDefault()))
                        .build())
            .toList();

    return new GetTagsResponse(tags);
  }

  @Transactional
  public void updateTag(Long tagId, UpdateTagRequest request) {
    Tag tag =
        tagRepository
            .findById(tagId)
            .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다: " + tagId));

    if (request.description() != null) {
      tag.updateDescription(request.description());
    }

    if (request.verified() != null) {
      if (request.verified()) {
        tag.verify();
      } else {
        tag.unverify();
      }
    }
  }

  @Transactional
  public void deleteTag(Long tagId) {
    Tag tag =
        tagRepository
            .findById(tagId)
            .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다: " + tagId));

    tagRepository.delete(tag);
  }
}
