package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.post.exception.TagAlreadyExistsException;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
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

  @Mock private ProjectDomainService projectDomainService;

  @Mock private TagMapper tagMapper;

  @InjectMocks private TagService tagService;

  @Test
  @DisplayName("[addTagsToPost] 태그 수가 최대 허용 개수를 초과하면 PostTagLimitExceededException 예외 발생")
  void addTagsToPost_tagsExceedLimit_throwsException() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

    assertThatThrownBy(() -> tagService.addTagsToPost(post, null, tags))
        .isInstanceOf(PostTagLimitExceededException.class);
  }

  @Test
  @DisplayName("[addTagsToPost] 이미 존재하는 태그는 새로 저장하지 않고 재사용")
  void addTagsToPost_existingTag_doesNotSaveNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.of(existingTag));

    tagService.addTagsToPost(post, null, List.of("java"));

    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  @DisplayName("[addTagsToPost] 존재하지 않는 태그는 새로 생성하여 저장")
  void addTagsToPost_newTag_savesNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag newTag = Tag.builder().name("spring").build();

    when(tagRepository.findByNameAndProjectIsNull("spring")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

    tagService.addTagsToPost(post, null, List.of("spring"));

    verify(tagRepository, times(1)).save(any(Tag.class));
  }

  @Test
  @DisplayName("[addTagsToPost] 각 태그에 대해 postCount를 1 증가")
  void addTagsToPost_incrementsPostCountForEachTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag tag1 = Tag.builder().name("java").build();
    Tag tag2 = Tag.builder().name("spring").build();

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.of(tag1));
    when(tagRepository.findByNameAndProjectIsNull("spring")).thenReturn(Optional.of(tag2));

    tagService.addTagsToPost(post, null, List.of("java", "spring"));

    verify(tagRepository, times(1)).incrementPostCount(tag1);
    verify(tagRepository, times(1)).incrementPostCount(tag2);
  }

  @Test
  @DisplayName("[addTagsToPost] 각 태그가 Post의 태그 목록에 추가됨")
  void addTagsToPost_addsTagsToPost() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag tag1 = Tag.builder().name("java").build();
    Tag tag2 = Tag.builder().name("spring").build();

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.of(tag1));
    when(tagRepository.findByNameAndProjectIsNull("spring")).thenReturn(Optional.of(tag2));

    tagService.addTagsToPost(post, null, List.of("java", "spring"));

    assertThat(post.getTags()).hasSize(2);
  }

  @Test
  @DisplayName("[addTagsToPost] 프로젝트가 주어지면 프로젝트에 속한 태그로 조회 및 생성")
  void addTagsToPost_withProject_usesProjectScopedTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Project project = Project.builder().handle("my-project").name("My Project").build();
    Tag newTag = Tag.builder().name("java").project(project).build();

    when(tagRepository.findByNameAndProject("java", project)).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

    tagService.addTagsToPost(post, project, List.of("java"));

    verify(tagRepository, never()).findByNameAndProjectIsNull(any());
    verify(tagRepository, times(1)).save(any(Tag.class));
  }

  @Test
  @DisplayName("[createTag] 존재하지 않는 이름이면 인증된 상태의 전역 태그를 생성")
  void createTag_newName_savesVerifiedTag() {
    Tag savedTag = Tag.builder().name("java").build();
    savedTag.verify();
    CreateTagResponse response = new CreateTagResponse(1L, "java", "", 0L);

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
    when(tagMapper.toCreateTagResponse(savedTag)).thenReturn(response);

    CreateTagResponse result = tagService.createTag(new CreateTagRequest("java"));

    assertThat(result).isEqualTo(response);
    verify(tagRepository, times(1)).save(any(Tag.class));
  }

  @Test
  @DisplayName("[createTag] 이미 존재하는 이름이면 TagAlreadyExistsException 예외 발생")
  void createTag_existingName_throwsException() {
    Tag existingTag = Tag.builder().name("java").build();

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.of(existingTag));

    assertThatThrownBy(() -> tagService.createTag(new CreateTagRequest("java")))
        .isInstanceOf(TagAlreadyExistsException.class);

    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  @DisplayName("[createProjectTag] 존재하지 않는 이름이면 인증된 상태의 프로젝트 태그를 생성")
  void createProjectTag_newName_savesVerifiedProjectTag() {
    String handle = "my-project";
    Project project = Project.builder().handle(handle).name("My Project").build();
    Tag savedTag = Tag.builder().name("java").project(project).build();
    savedTag.verify();
    CreateTagResponse response = new CreateTagResponse(1L, "java", "", 0L);

    when(projectDomainService.getProjectByHandle(handle)).thenReturn(project);
    when(tagRepository.findByNameAndProject("java", project)).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
    when(tagMapper.toCreateTagResponse(savedTag)).thenReturn(response);

    CreateTagResponse result = tagService.createProjectTag(handle, new CreateTagRequest("java"));

    assertThat(result).isEqualTo(response);
    verify(tagRepository, times(1)).save(any(Tag.class));
  }

  @Test
  @DisplayName("[createProjectTag] 프로젝트에 이미 존재하는 이름이면 TagAlreadyExistsException 예외 발생")
  void createProjectTag_existingName_throwsException() {
    String handle = "my-project";
    Project project = Project.builder().handle(handle).name("My Project").build();
    Tag existingTag = Tag.builder().name("java").project(project).build();

    when(projectDomainService.getProjectByHandle(handle)).thenReturn(project);
    when(tagRepository.findByNameAndProject("java", project)).thenReturn(Optional.of(existingTag));

    assertThatThrownBy(() -> tagService.createProjectTag(handle, new CreateTagRequest("java")))
        .isInstanceOf(TagAlreadyExistsException.class);

    verify(tagRepository, never()).save(any(Tag.class));
  }
}
