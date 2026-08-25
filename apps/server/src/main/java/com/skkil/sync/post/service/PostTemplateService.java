package com.skkil.sync.post.service;

import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.request.CreatePostTemplateRequest;
import com.skkil.sync.post.dto.request.UpdatePostTemplateRequest;
import com.skkil.sync.post.dto.response.CreatePostTemplateResponse;
import com.skkil.sync.post.dto.response.GetPostTemplatesResponse;
import com.skkil.sync.post.exception.PostTemplateAlreadyExistsException;
import com.skkil.sync.post.exception.PostTemplateLimitExceededException;
import com.skkil.sync.post.exception.PostTemplateNotFoundException;
import com.skkil.sync.post.mapper.PostTemplateAssembler;
import com.skkil.sync.post.model.PostTemplate;
import com.skkil.sync.post.repository.PostTemplateRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 템플릿 관리(생성·수정·삭제)는 프로젝트 관리자만, 사용(조회)은 글을 쓸 수 있는 팀원 누구나 — 프로젝트 태그와 같은 정책이다. */
@Service
public class PostTemplateService {

  private final UserDomainService userDomainService;
  private final ProjectDomainService projectDomainService;
  private final PostTemplateRepository templateRepository;
  private final PostTemplateAssembler templateAssembler;

  public PostTemplateService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      PostTemplateRepository templateRepository,
      PostTemplateAssembler templateAssembler) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.templateRepository = templateRepository;
    this.templateAssembler = templateAssembler;
  }

  /** 템플릿은 글쓰기 도구이므로 읽기(READ)가 아니라 글쓰기 권한(CREATE)으로 게이트한다 — 비공개 프로젝트의 템플릿이 외부에 새지 않는다. */
  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'CREATE')")
  public GetPostTemplatesResponse getProjectTemplates(String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    return templateAssembler.toGetTemplatesResponse(
        templateRepository.findByProjectIdOrderByNameAsc(project.getId()));
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public CreatePostTemplateResponse createTemplate(
      Long userId, String handle, CreatePostTemplateRequest request) {
    Project project = projectDomainService.getProjectByHandle(handle);

    if (templateRepository.countByProjectId(project.getId())
        >= PostConstants.MAX_POST_TEMPLATES_PER_PROJECT) {
      throw new PostTemplateLimitExceededException();
    }

    String name = request.name().trim();
    if (templateRepository.existsByProjectIdAndName(project.getId(), name)) {
      throw new PostTemplateAlreadyExistsException(name);
    }

    PostTemplate template =
        PostTemplate.builder()
            .externalId(Slugify.slugify(name))
            .creator(userDomainService.getUserReference(userId))
            .project(project)
            .name(name)
            .title(request.title())
            .content(request.content())
            .build();

    return new CreatePostTemplateResponse(templateRepository.save(template).getExternalId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void updateTemplate(String handle, String externalId, UpdatePostTemplateRequest request) {
    PostTemplate template = findTemplate(handle, externalId);

    String newName = request.name().trim();
    if (!newName.equals(template.getName())
        && templateRepository.existsByProjectIdAndName(template.getProject().getId(), newName)) {
      throw new PostTemplateAlreadyExistsException(newName);
    }

    template.update(newName, request.title(), request.content());
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void deleteTemplate(String handle, String externalId) {
    templateRepository.delete(findTemplate(handle, externalId));
  }

  // 조회 자체를 핸들의 프로젝트로 스코프해, 다른 프로젝트의 템플릿에는 구조적으로 접근할 수 없게 한다
  // (권한만으로는 호출자가 "그 핸들의" 프로젝트 관리자라는 것까지만 보장된다 — TagService.findTag 와 같은 원칙).
  private PostTemplate findTemplate(String handle, String externalId) {
    Project project = projectDomainService.getProjectByHandle(handle);
    return templateRepository
        .findByProjectIdAndExternalId(project.getId(), externalId)
        .orElseThrow(() -> new PostTemplateNotFoundException(externalId));
  }
}
