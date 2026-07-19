package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.request.MergeTagsRequest;
import com.skkil.sync.post.dto.request.UpdateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetAllTagsResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.dto.summary.TagSummary;
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
import com.skkil.sync.post.repository.PostTagRepository;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final TagFollowRelationshipRepository tagFollowRelationshipRepository;
  private final ProjectDomainService projectDomainService;
  private final TagMapper tagMapper;
  private final PaginationService paginationService;

  public TagService(
      TagRepository tagRepository,
      PostTagRepository postTagRepository,
      TagFollowRelationshipRepository tagFollowRelationshipRepository,
      ProjectDomainService projectDomainService,
      TagMapper tagMapper,
      PaginationService paginationService) {
    this.tagRepository = tagRepository;
    this.postTagRepository = postTagRepository;
    this.tagFollowRelationshipRepository = tagFollowRelationshipRepository;
    this.projectDomainService = projectDomainService;
    this.tagMapper = tagMapper;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#id, 'TAG', 'READ')")
  public TagSummary getTag(Long requesterId, Long id) {
    Tag tag = tagRepository.findByIdWithProject(id).orElseThrow(() -> new TagNotFoundException(id));
    return tagMapper.toTagSummary(tag, followedTagIds(requesterId));
  }

  @Transactional(readOnly = true)
  public Map<Long, List<TagSummary>> getTagsForPosts(Long requesterId, List<Long> postIds) {
    if (postIds.isEmpty()) {
      return Map.of();
    }

    Set<Long> followedTagIds = followedTagIds(requesterId);

    return postTagRepository.findByPostIdIn(postIds).stream()
        .collect(
            Collectors.groupingBy(
                postTag -> postTag.getPost().getId(),
                Collectors.mapping(
                    postTag -> tagMapper.toTagSummary(postTag.getTag(), followedTagIds),
                    Collectors.toList())));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("#handle == null or hasPermission(#handle, 'PROJECT', 'READ')")
  public GetTagsResponse searchTags(Long requesterId, @Nullable String handle, String query) {
    Set<Long> followedTagIds = followedTagIds(requesterId);

    List<TagSummary> tags = new ArrayList<>(searchGlobalTags(query, followedTagIds));
    if (handle != null) {
      tags.addAll(searchProjectTags(handle, query, followedTagIds));
    }

    return new GetTagsResponse(tags);
  }

  private List<TagSummary> searchGlobalTags(String query, Set<Long> followedTagIds) {
    return tagRepository.searchTags(query, PostStatus.PUBLISHED, PostVisibility.VISIBLE).stream()
        .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
        .toList();
  }

  private List<TagSummary> searchProjectTags(
      String handle, String query, Set<Long> followedTagIds) {
    Project project = projectDomainService.getProjectByHandle(handle);

    return tagRepository.searchTagsByProject(project, query).stream()
        .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
        .toList();
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'READ')")
  public GetTagsResponse getProjectTags(Long requesterId, String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    Set<Long> followedTagIds = followedTagIds(requesterId);

    var tags =
        tagRepository.findByProject(project).stream()
            .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
            .toList();

    return new GetTagsResponse(tags);
  }

  @Transactional(readOnly = true)
  public GetAllTagsResponse getAllTags(Long requesterId, OffsetPaginationRequest pagination) {
    Set<Long> followedTagIds = followedTagIds(requesterId);

    var page =
        paginationService.paginate(
            tagRepository::findByProjectIsNullAndVerifiedTrueOrderByNameAsc, pagination);

    return new GetAllTagsResponse(page.map(tag -> tagMapper.toTagSummary(tag, followedTagIds)));
  }

  @Transactional
  public void addTagsToPost(
      Post post, @Nullable Project project, List<String> tagNames, List<String> projectTagNames) {
    List<String> filteredTagNames = filterTagNames(tagNames);
    List<String> filteredProjectTagNames =
        project == null ? List.of() : filterTagNames(projectTagNames);

    if (filteredTagNames.size() + filteredProjectTagNames.size()
        > PostConstants.MAX_TAGS_PER_POST) {
      throw new PostTagLimitExceededException();
    }

    attachGlobalTags(post, filteredTagNames);
    attachProjectTags(post, project, filteredProjectTagNames);
  }

  @Transactional
  public void replaceTags(Post post, List<String> tags, List<String> projectTags) {
    List<PostTag> currentTags = List.copyOf(post.getTags());
    List<String> filteredTags = filterTagNames(tags);
    List<String> filteredProjectTags =
        post.getProject() == null ? List.of() : filterTagNames(projectTags);
    if (filteredTags.size() + filteredProjectTags.size() > PostConstants.MAX_TAGS_PER_POST) {
      throw new PostTagLimitExceededException();
    }

    Set<String> requestedTagNames = Set.copyOf(filteredTags);
    Set<String> requestedProjectTagNames = Set.copyOf(filteredProjectTags);

    Set<String> currentTagNames =
        currentTags.stream()
            .map(PostTag::getTag)
            .filter(tag -> tag.getProject() == null)
            .map(Tag::getName)
            .collect(Collectors.toSet());
    Set<String> currentProjectTagNames =
        currentTags.stream()
            .map(PostTag::getTag)
            .filter(tag -> tag.getProject() != null)
            .map(Tag::getName)
            .collect(Collectors.toSet());

    for (PostTag postTag : currentTags) {
      Tag tag = postTag.getTag();
      Set<String> requestedNames =
          tag.getProject() == null ? requestedTagNames : requestedProjectTagNames;
      if (!requestedNames.contains(tag.getName())) {
        post.removeTag(tag);
        tagRepository.decrementPostCount(tag);
      }
    }

    List<String> tagsToAdd =
        filteredTags.stream().filter(tag -> !currentTagNames.contains(tag)).toList();
    List<String> projectTagsToAdd =
        filteredProjectTags.stream().filter(tag -> !currentProjectTagNames.contains(tag)).toList();

    attachGlobalTags(post, tagsToAdd);
    attachProjectTags(post, post.getProject(), projectTagsToAdd);
  }

  private void attachGlobalTags(Post post, List<String> names) {
    for (String name : names) {
      Tag tag =
          tagRepository
              .findByNameAndProjectIsNull(name)
              .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
      attachTag(post, tag);
    }
  }

  private void attachProjectTags(Post post, @Nullable Project project, List<String> names) {
    for (String name : names) {
      Tag tag =
          tagRepository
              .findByNameAndProject(name, project)
              .orElseGet(
                  () -> tagRepository.save(Tag.builder().name(name).project(project).build()));
      attachTag(post, tag);
    }
  }

  private void attachTag(Post post, Tag tag) {
    PostTag postTag = PostTag.builder().post(post).tag(tag).build();
    post.addTag(postTag);
    tagRepository.incrementPostCount(tag);
  }

  private static List<String> filterTagNames(@Nullable List<String> tagNames) {
    if (tagNames == null) {
      return List.of();
    }

    return tagNames.stream()
        .filter(tag -> tag != null)
        .map(String::trim)
        .filter(tag -> !tag.isBlank())
        .distinct()
        .toList();
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public GetTagsResponse getUnverifiedTags(Long requesterId) {
    Set<Long> followedTagIds = followedTagIds(requesterId);

    var tags =
        tagRepository.findUnverifiedTags().stream()
            .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
            .toList();

    return new GetTagsResponse(tags);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public GetTagsResponse getProjectUnverifiedTags(Long requesterId, String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    Set<Long> followedTagIds = followedTagIds(requesterId);

    var tags =
        tagRepository.findUnverifiedTagsByProject(project).stream()
            .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
            .toList();

    return new GetTagsResponse(tags);
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void verifyTag(String name) {
    Tag tag = findTag(name, null);
    tag.verify();
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void verifyProjectTag(String handle, String name) {
    Tag tag = findTag(name, handle);
    tag.verify();
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public CreateTagResponse createTag(CreateTagRequest request) {
    return createTag(null, request);
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public CreateTagResponse createProjectTag(String handle, CreateTagRequest request) {
    Project project = projectDomainService.getProjectByHandle(handle);
    return createTag(project, request);
  }

  private CreateTagResponse createTag(@Nullable Project project, CreateTagRequest request) {
    String name = request.name().trim();

    boolean exists =
        project == null
            ? tagRepository.findByNameAndProjectIsNull(name).isPresent()
            : tagRepository.findByNameAndProject(name, project).isPresent();
    if (exists) {
      throw new TagAlreadyExistsException(name);
    }

    Tag tag = Tag.builder().name(name).project(project).build();
    tag.verify();
    return tagMapper.toCreateTagResponse(tagRepository.save(tag));
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void updateTag(String name, UpdateTagRequest request) {
    updateTag(name, null, request);
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void updateProjectTag(String handle, String name, UpdateTagRequest request) {
    updateTag(name, handle, request);
  }

  private void updateTag(String name, @Nullable String handle, UpdateTagRequest request) {
    Tag tag = findTag(name, handle);

    String newName = request.name().trim();
    if (!newName.equals(tag.getName())) {
      Project project = tag.getProject();
      boolean exists =
          project == null
              ? tagRepository.findByNameAndProjectIsNull(newName).isPresent()
              : tagRepository.findByNameAndProject(newName, project).isPresent();
      if (exists) {
        throw new TagAlreadyExistsException(newName);
      }
      tag.updateName(newName);
    }

    tag.updateDescription(request.description());
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void rejectTag(String name) {
    deleteTag(name, null);
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void rejectProjectTag(String handle, String name) {
    deleteTag(name, handle);
  }

  private void deleteTag(String name, @Nullable String handle) {
    tagRepository.delete(findTag(name, handle));
  }

  // Scoping the lookup itself by project (rather than fetching by a caller-supplied ID and
  // checking ownership after the fact) makes cross-project access structurally impossible: a
  // manager of project B can never resolve project A's tag through this method, since the query
  // is bound to B's project from the start.
  private Tag findTag(String name, @Nullable String handle) {
    if (handle == null) {
      return tagRepository
          .findByNameAndProjectIsNull(name)
          .orElseThrow(() -> new TagNotFoundException(name));
    }

    Project project = projectDomainService.getProjectByHandle(handle);
    return tagRepository
        .findByNameAndProject(name, project)
        .orElseThrow(() -> new TagNotFoundException(name));
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void mergeTags(MergeTagsRequest request) {
    Long sourceTagId = request.sourceTagId();
    Long targetTagId = request.targetTagId();

    Tag source =
        tagRepository
            .findByIdWithProject(sourceTagId)
            .orElseThrow(() -> new TagNotFoundException(sourceTagId));
    Tag target =
        tagRepository
            .findByIdWithProject(targetTagId)
            .orElseThrow(() -> new TagNotFoundException(targetTagId));

    if (!inSamePool(source, target)) {
      throw new TagPoolMismatchException(sourceTagId, targetTagId);
    }

    tagRepository.deleteDuplicatePostTags(sourceTagId, targetTagId);
    tagRepository.reassignPostTags(sourceTagId, targetTagId);
    tagRepository.deleteDuplicateTagFollows(sourceTagId, targetTagId);
    tagRepository.reassignTagFollows(sourceTagId, targetTagId);
    // Recompute rather than sum the old counts — duplicate rows get dropped above, so a naive
    // sum would overcount posts/followers that already referenced both tags.
    tagRepository.recomputeCounts(targetTagId);

    tagRepository.delete(source);
  }

  private static boolean inSamePool(Tag a, Tag b) {
    if (a.getProject() == null || b.getProject() == null) {
      return a.getProject() == null && b.getProject() == null;
    }

    return a.getProject().getId().equals(b.getProject().getId());
  }

  private Set<Long> followedTagIds(Long requesterId) {
    return requesterId == null
        ? Set.of()
        : tagFollowRelationshipRepository.findTagIdsByFollowerId(requesterId);
  }
}
