package com.skkil.sync.post.service;

import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.response.SearchTagsResponse;
import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;

  public TagService(TagRepository tagRepository, TagMapper tagMapper) {
    this.tagRepository = tagRepository;
    this.tagMapper = tagMapper;
  }

  @Transactional(readOnly = true)
  public SearchTagsResponse searchTags(String query) {
    var tags = tagRepository.searchTags(query).stream().map(tagMapper::toTag).toList();

    return new SearchTagsResponse(tags);
  }

  @Transactional
  public void addTagsToPost(Post post, List<String> tags) {
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

    if (filteredTags.size() > PostConstants.MAX_TAGS_PER_POST) {
      throw new PostTagLimitExceededException();
    }

    for (String name : filteredTags) {
      Tag tag =
          tagRepository
              .findByName(name)
              .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));

      PostTag postTag = PostTag.builder().post(post).tag(tag).build();
      post.addTag(postTag);
      tagRepository.incrementPostCount(tag);
    }
  }
}
