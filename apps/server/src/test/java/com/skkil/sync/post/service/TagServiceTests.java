package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.request.MergeTagsRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.post.exception.TagAlreadyExistsException;
import com.skkil.sync.post.exception.TagNotFoundException;
import com.skkil.sync.post.exception.TagPoolMismatchException;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTests {

  @Mock private TagRepository tagRepository;

  @Mock private TagFollowRelationshipRepository tagFollowRelationshipRepository;

  @Mock private ProjectDomainService projectDomainService;

  @Mock private TagMapper tagMapper;

  @InjectMocks private TagService tagService;

  @Test
  @DisplayName("[addTagsToPost] 태그 수가 최대 허용 개수를 초과하면 PostTagLimitExceededException 예외 발생")
  void addTagsToPost_tagsExceedLimit_throwsException() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

    assertThatThrownBy(() -> tagService.addTagsToPost(post, null, tags, List.of()))
        .isInstanceOf(PostTagLimitExceededException.class);
  }

  @Test
  @DisplayName("[addTagsToPost] 이미 존재하는 태그는 새로 저장하지 않고 재사용")
  void addTagsToPost_existingTag_doesNotSaveNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();

    when(tagRepository.findByNameAndProjectIsNull("java")).thenReturn(Optional.of(existingTag));

    tagService.addTagsToPost(post, null, List.of("java"), List.of());

    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  @DisplayName("[addTagsToPost] 존재하지 않는 태그는 새로 생성하여 저장")
  void addTagsToPost_newTag_savesNewTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag newTag = Tag.builder().name("spring").build();

    when(tagRepository.findByNameAndProjectIsNull("spring")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

    tagService.addTagsToPost(post, null, List.of("spring"), List.of());

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

    tagService.addTagsToPost(post, null, List.of("java", "spring"), List.of());

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

    tagService.addTagsToPost(post, null, List.of("java", "spring"), List.of());

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

    tagService.addTagsToPost(post, project, List.of(), List.of("java"));

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

  @Test
  @DisplayName("[replaceTags] 기존 태그를 유지하는 수정은 태그를 다시 추가하지 않음")
  void replaceTags_keepsExistingTag_doesNotReAddTag() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();
    post.addTag(PostTag.builder().post(post).tag(existingTag).build());

    tagService.replaceTags(post, List.of("java"), List.of());

    assertThat(post.getTags()).hasSize(1);
    assertThat(post.getTags().get(0).getTag()).isSameAs(existingTag);
    verify(tagRepository, never()).findByNameAndProjectIsNull(any(String.class));
    verify(tagRepository, never()).save(any(Tag.class));
    verify(tagRepository, never()).incrementPostCount(any(Tag.class));
    verify(tagRepository, never()).decrementPostCount(any(Tag.class));
  }

  @Test
  @DisplayName("[replaceTags] 제거된 태그만 감소시키고 새 전역 태그만 추가")
  void replaceTags_removesMissingTagsAndAddsNewGlobalTags() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag keptTag = Tag.builder().name("java").build();
    Tag removedTag = Tag.builder().name("legacy").build();
    Tag addedTag = Tag.builder().name("spring").build();
    post.addTag(PostTag.builder().post(post).tag(keptTag).build());
    post.addTag(PostTag.builder().post(post).tag(removedTag).build());

    when(tagRepository.findByNameAndProjectIsNull("spring")).thenReturn(Optional.of(addedTag));

    tagService.replaceTags(post, List.of("java", "spring"), List.of());

    assertThat(post.getTags())
        .extracting(postTag -> postTag.getTag().getName())
        .containsExactly("java", "spring");
    verify(tagRepository, times(1)).decrementPostCount(removedTag);
    verify(tagRepository, never()).decrementPostCount(keptTag);
    verify(tagRepository, times(1)).incrementPostCount(addedTag);
    verify(tagRepository, never()).incrementPostCount(keptTag);
    verify(tagRepository, never()).findByNameAndProjectIsNull("java");
    verify(tagRepository, times(1)).findByNameAndProjectIsNull("spring");
  }

  @Test
  @DisplayName("[replaceTags] Workspace 글은 프로젝트 범위 태그를 사용")
  void replaceTags_workspacePost_usesProjectScopedTags() {
    Project project = Project.builder().handle("workspace").name("Workspace").build();
    Post post = Post.builder().slug("slug").title("제목").content("내용").project(project).build();
    Tag addedTag = Tag.builder().name("spring").project(project).build();

    when(tagRepository.findByNameAndProject("spring", project)).thenReturn(Optional.of(addedTag));

    tagService.replaceTags(post, List.of(), List.of("spring"));

    assertThat(post.getTags()).extracting(postTag -> postTag.getTag()).containsExactly(addedTag);
    verify(tagRepository, times(1)).findByNameAndProject("spring", project);
    verify(tagRepository, never()).findByNameAndProjectIsNull(any(String.class));
    verify(tagRepository, times(1)).incrementPostCount(addedTag);
  }

  @Test
  @DisplayName("[replaceTags] 빈 태그 목록이면 기존 태그를 모두 제거")
  void replaceTags_emptyTags_removesAllTags() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();
    post.addTag(PostTag.builder().post(post).tag(existingTag).build());

    tagService.replaceTags(post, List.of(), List.of());

    assertThat(post.getTags()).isEmpty();
    verify(tagRepository, times(1)).decrementPostCount(existingTag);
    verify(tagRepository, never()).incrementPostCount(any(Tag.class));
  }

  @Test
  @DisplayName("[replaceTags] 태그 제한 초과 시 기존 태그를 변경하지 않음")
  void replaceTags_tagsExceedLimit_doesNotMutateExistingTags() {
    Post post = Post.builder().slug("slug").title("제목").content("내용").build();
    Tag existingTag = Tag.builder().name("java").build();
    post.addTag(PostTag.builder().post(post).tag(existingTag).build());
    List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

    assertThatThrownBy(() -> tagService.replaceTags(post, tags, List.of()))
        .isInstanceOf(PostTagLimitExceededException.class);

    assertThat(post.getTags()).hasSize(1);
    assertThat(post.getTags().get(0).getTag()).isSameAs(existingTag);
    verify(tagRepository, never()).decrementPostCount(any(Tag.class));
    verify(tagRepository, never()).incrementPostCount(any(Tag.class));
  }

  @Test
  @DisplayName("[searchTags] 팔로우한 태그 ID 집합을 조회하여 각 태그의 isFollowing 매핑에 사용")
  void searchTags_buildsFollowedTagIdsOnce() {
    String query = "java";
    Set<Long> followedTagIds = Set.of(5L);

    when(tagFollowRelationshipRepository.findTagIdsByFollowerId(1L)).thenReturn(followedTagIds);
    when(tagRepository.searchTags(query, PostStatus.PUBLISHED, PostVisibility.VISIBLE))
        .thenReturn(List.of());

    tagService.searchTags(1L, null, query);

    verify(tagFollowRelationshipRepository, times(1)).findTagIdsByFollowerId(1L);
  }

  @Test
  @DisplayName("[rejectTag] 전역 태그를 삭제")
  void rejectTag_deletesTag() {
    String name = "java";
    Tag tag = Tag.builder().name(name).build();

    when(tagRepository.findByNameAndProjectIsNull(name)).thenReturn(Optional.of(tag));

    tagService.rejectTag(name);

    verify(tagRepository, times(1)).delete(tag);
  }

  @Test
  @DisplayName("[rejectProjectTag] 요청한 프로젝트 소유의 태그를 삭제")
  void rejectProjectTag_ownTag_deletesTag() {
    String name = "java";
    String handle = "my-project";
    Project project = Project.builder().handle(handle).name("My Project").build();
    Tag tag = Tag.builder().name(name).project(project).build();

    when(projectDomainService.getProjectByHandle(handle)).thenReturn(project);
    when(tagRepository.findByNameAndProject(name, project)).thenReturn(Optional.of(tag));

    tagService.rejectProjectTag(handle, name);

    verify(tagRepository, times(1)).delete(tag);
  }

  @Test
  @DisplayName("[rejectProjectTag] 다른 프로젝트 소유의 태그를 삭제하려 하면 TagNotFoundException 예외 발생")
  void rejectProjectTag_otherProjectsTag_throwsException() {
    String name = "java";
    String handle = "my-project";
    Project project = Project.builder().handle(handle).name("My Project").build();

    when(projectDomainService.getProjectByHandle(handle)).thenReturn(project);
    when(tagRepository.findByNameAndProject(name, project)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> tagService.rejectProjectTag(handle, name))
        .isInstanceOf(TagNotFoundException.class);

    verify(tagRepository, never()).delete(any(Tag.class));
  }

  @Test
  @DisplayName("[mergeTags] 동일한 풀에 속한 두 태그를 병합")
  void mergeTags_samePool_mergesTags() {
    Long sourceId = 1L;
    Long targetId = 2L;
    Tag source = Tag.builder().name("java").build();
    Tag target = Tag.builder().name("Java").build();

    when(tagRepository.findByIdWithProject(sourceId)).thenReturn(Optional.of(source));
    when(tagRepository.findByIdWithProject(targetId)).thenReturn(Optional.of(target));

    tagService.mergeTags(new MergeTagsRequest(sourceId, targetId));

    verify(tagRepository, times(1)).deleteDuplicatePostTags(sourceId, targetId);
    verify(tagRepository, times(1)).reassignPostTags(sourceId, targetId);
    verify(tagRepository, times(1)).deleteDuplicateTagFollows(sourceId, targetId);
    verify(tagRepository, times(1)).reassignTagFollows(sourceId, targetId);
    verify(tagRepository, times(1)).recomputeCounts(targetId);
    verify(tagRepository, times(1)).delete(source);
  }

  @Test
  @DisplayName("[mergeTags] 서로 다른 풀에 속한 태그를 병합하려 하면 TagPoolMismatchException 예외 발생")
  void mergeTags_differentPools_throwsException() {
    Long sourceId = 1L;
    Long targetId = 2L;
    Project project = Project.builder().handle("my-project").name("My Project").build();
    Tag source = Tag.builder().name("java").build();
    Tag target = Tag.builder().name("java").project(project).build();

    when(tagRepository.findByIdWithProject(sourceId)).thenReturn(Optional.of(source));
    when(tagRepository.findByIdWithProject(targetId)).thenReturn(Optional.of(target));

    assertThatThrownBy(() -> tagService.mergeTags(new MergeTagsRequest(sourceId, targetId)))
        .isInstanceOf(TagPoolMismatchException.class);

    verify(tagRepository, never()).deleteDuplicatePostTags(any(), any());
    verify(tagRepository, never()).delete(any(Tag.class));
  }
}
