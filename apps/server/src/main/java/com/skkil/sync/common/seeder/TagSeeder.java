package com.skkil.sync.common.seeder;

import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class TagSeeder {

  private final TagRepository tagRepository;

  TagSeeder(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  boolean hasGlobalTags() {
    return tagRepository.existsByProjectIsNull();
  }

  void seed(TagSeed tagSeed) {
    Tag tag =
        tagRepository
            .findByNameAndProjectIsNull(tagSeed.name())
            .orElseGet(
                () ->
                    Tag.builder().name(tagSeed.name()).description(tagSeed.description()).build());

    tag.verify();

    tagRepository.save(tag);
  }

  public static record TagSeed(String name, String description) {}
}
