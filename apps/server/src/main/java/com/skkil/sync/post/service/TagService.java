package com.skkil.sync.post.service;

import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.post.exception.TagAlreadyExistsException;
import com.skkil.sync.post.exception.TagNotFoundException;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final ProjectDomainService projectDomainService;
  private final TagMapper tagMapper;

  public TagService(
      TagRepository tagRepository, ProjectDomainService projectDomainService, TagMapper tagMapper) {
    this.tagRepository = tagRepository;
    this.projectDomainService = projectDomainService;
    this.tagMapper = tagMapper;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("#handle == null or hasPermission(#handle, 'PROJECT', 'READ')")
  public GetTagsResponse searchTags(@Nullable String handle, String query) {
    Project project = handle == null ? null : projectDomainService.getProjectByHandle(handle);

    var tags =
        (project == null
                ? tagRepository.searchTags(query)
                : tagRepository.searchTagsByProject(project, query))
            .stream().map(tagMapper::toTag).toList();

    return new GetTagsResponse(tags);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'READ')")
  public GetTagsResponse getProjectTags(String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);

    var tags = tagRepository.findByProject(project).stream().map(tagMapper::toTag).toList();

    return new GetTagsResponse(tags);
  }

  @Transactional
  public void addTagsToPost(Post post, @Nullable Project project, List<String> tags) {
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
          project == null
              ? tagRepository
                  .findByNameAndProjectIsNull(name)
                  .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()))
              : tagRepository
                  .findByNameAndProject(name, project)
                  .orElseGet(
                      () -> tagRepository.save(Tag.builder().name(name).project(project).build()));

      PostTag postTag = PostTag.builder().post(post).tag(tag).build();
      post.addTag(postTag);
      tagRepository.incrementPostCount(tag);
    }
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public GetTagsResponse getUnverifiedTags() {
    var tags = tagRepository.findUnverifiedTags().stream().map(tagMapper::toTag).toList();

    return new GetTagsResponse(tags);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public GetTagsResponse getProjectUnverifiedTags(String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);

    var tags =
        tagRepository.findUnverifiedTagsByProject(project).stream().map(tagMapper::toTag).toList();

    return new GetTagsResponse(tags);
  }

  @Transactional
  @PreAuthorize("hasPermission(#tagId, 'TAG', 'EDIT')")
  public void verifyTag(Long tagId) {
    Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));
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
}
