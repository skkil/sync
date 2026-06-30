package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTests {

  @Mock private TagRepository tagRepository;

  @InjectMocks private TagService tagService;

  @Test
  @DisplayName("[addTagsToPost] 태그 수가 최대 허용 개수를 초과하면 PostTagLimitExceededException 예외 발생")
  void addTagsToPost_tagsExceedLimit_throwsException() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

    assertThatThrownBy(() -> tagService.addTagsToPost(post, tags))
        .isInstanceOf(PostTagLimitExceededException.class);
  }

  @Test
  @DisplayName("[addTagsToPost] 이미 존재하는 태그는 새로 저장하지 않고 재사용")
  void addTagsToPost_existingTag_doesNotSaveNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();

    when(tagRepository.findByName("java")).thenReturn(Optional.of(existingTag));

    tagService.addTagsToPost(post, List.of("java"));

    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  @DisplayName("[addTagsToPost] 존재하지 않는 태그는 새로 생성하여 저장")
  void addTagsToPost_newTag_savesNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag newTag = Tag.builder().name("spring").build();

    when(tagRepository.findByName("spring")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

    tagService.addTagsToPost(post, List.of("spring"));

    verify(tagRepository, times(1)).save(any(Tag.class));
  }

  @Test
  @DisplayName("[addTagsToPost] 각 태그에 대해 postCount를 1 증가")
  void addTagsToPost_incrementsPostCountForEachTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag tag1 = Tag.builder().name("java").build();
    Tag tag2 = Tag.builder().name("spring").build();

    when(tagRepository.findByName("java")).thenReturn(Optional.of(tag1));
    when(tagRepository.findByName("spring")).thenReturn(Optional.of(tag2));

    tagService.addTagsToPost(post, List.of("java", "spring"));

    verify(tagRepository, times(1)).incrementPostCount(tag1);
    verify(tagRepository, times(1)).incrementPostCount(tag2);
  }

  @Test
  @DisplayName("[addTagsToPost] 각 태그가 Post의 태그 목록에 추가됨")
  void addTagsToPost_addsTagsToPost() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag tag1 = Tag.builder().name("java").build();
    Tag tag2 = Tag.builder().name("spring").build();

    when(tagRepository.findByName("java")).thenReturn(Optional.of(tag1));
    when(tagRepository.findByName("spring")).thenReturn(Optional.of(tag2));

    tagService.addTagsToPost(post, List.of("java", "spring"));

    assertThat(post.getTags()).hasSize(2);
  }
}
