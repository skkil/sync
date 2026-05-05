package com.skkil.sync.reflection.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.reflection.dto.response.GetReflectionResponse;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.mapper.ReflectionMapper;
import com.skkil.sync.reflection.repository.ReflectionQueryRepository;
import com.skkil.sync.reflection.repository.pagination.ReflectionCursorPaginationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReflectionQueryService {

  private final ReflectionQueryRepository reflectionQueryRepository;
  private final ReflectionContentMediaService contentMediaService;
  private final ReflectionMapper reflectionMapper;

  private final PaginationService paginationService;
  private final ReflectionCursorPaginationProvider paginationProvider;

  public ReflectionQueryService(
      ReflectionQueryRepository reflectionQueryRepository,
      ReflectionContentMediaService contentMediaService,
      ReflectionMapper reflectionMapper,
      ReflectionCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.reflectionQueryRepository = reflectionQueryRepository;
    this.contentMediaService = contentMediaService;
    this.reflectionMapper = reflectionMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetReflectionsResponse getReflections(CursorPaginationRequest pagination) {
    var reflections =
        paginationService
            .paginate(reflectionQueryRepository.getReflections(), paginationProvider, pagination)
            .map(reflectionMapper::toReflectionResponse)
            .map(this::resolveImageUrls);

    return new GetReflectionsResponse(reflections);
  }

  @Transactional(readOnly = true)
  public GetReflectionResponse getReflectionBySlug(String slug) {
    var reflection =
        reflectionQueryRepository
            .getReflectionBySlug(slug)
            .orElseThrow(() -> new ReflectionNotFoundException(slug));

    return resolveImageUrls(reflectionMapper.toGetReflectionResponse(reflection));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#userId, 'PROFILE', 'READ')")
  public GetReflectionsResponse getUserReflections(
      Long userId, CursorPaginationRequest pagination) {
    var reflections =
        paginationService
            .paginate(
                reflectionQueryRepository.getReflectionsByUser(userId),
                paginationProvider,
                pagination)
            .map(reflectionMapper::toReflectionResponse)
            .map(this::resolveImageUrls);

    return new GetReflectionsResponse(reflections);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'READ')")
  public GetReflectionsResponse getProjectExperienceReflections(
      Long experienceId, CursorPaginationRequest pagination) {
    var reflections =
        paginationService
            .paginate(
                reflectionQueryRepository.getReflectionsByExperience(experienceId),
                paginationProvider,
                pagination)
            .map(reflectionMapper::toReflectionResponse)
            .map(this::resolveImageUrls);

    return new GetReflectionsResponse(reflections);
  }

  private GetReflectionResponse resolveImageUrls(GetReflectionResponse reflection) {
    return new GetReflectionResponse(
        reflection.id(),
        reflection.author(),
        contentMediaService.resolveImageUrls(reflection.content()),
        reflection.likeCount(),
        reflection.commentCount());
  }

  private GetReflectionsResponse.Reflection resolveImageUrls(
      GetReflectionsResponse.Reflection reflection) {
    return new GetReflectionsResponse.Reflection(
        reflection.id(),
        reflection.author(),
        reflection.project(),
        contentMediaService.resolveImageUrls(reflection.content()),
        reflection.createdAt());
  }
}
