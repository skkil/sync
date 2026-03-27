package com.skkil.sync.reflection.service.query;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import com.skkil.sync.reflection.mapper.ReflectionMapper;
import com.skkil.sync.reflection.repository.query.ReflectionQueryRepository;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReflectionQueryService {

  private final ReflectionQueryRepository reflectionQueryRepository;
  private final ReflectionMapper reflectionMapper;
  private final CursorConverter cursorConverter;

  public ReflectionQueryService(
      ReflectionQueryRepository reflectionQueryRepository,
      ReflectionMapper reflectionMapper,
      CursorConverter cursorConverter) {
    this.reflectionQueryRepository = reflectionQueryRepository;
    this.reflectionMapper = reflectionMapper;
    this.cursorConverter = cursorConverter;
  }

  @Transactional(readOnly = true)
  public GetReflectionsResponse getReflections(CursorPaginationRequest pagination) {
    List<ReflectionDto> reflections =
        reflectionQueryRepository.getReflections(
            cursorConverter.decode(pagination.cursor()), pagination.size() + 1);
    log.debug("Fetched {} reflections", reflections.size());

    return new GetReflectionsResponse(
        mapToCursorPaginationResponse(reflections, pagination.size()));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#userId, 'PROFILE', 'READ')")
  public GetReflectionsResponse getUserReflections(
      Long userId, CursorPaginationRequest pagination) {
    List<ReflectionDto> reflections =
        reflectionQueryRepository.getReflectionsByUser(
            userId, cursorConverter.decode(pagination.cursor()), pagination.size() + 1);
    log.debug("Fetched {} reflections for user {}", reflections.size(), userId);

    return new GetReflectionsResponse(
        mapToCursorPaginationResponse(reflections, pagination.size()));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'READ')")
  public GetReflectionsResponse getProjectExperienceReflections(
      Long experienceId, CursorPaginationRequest pagination) {
    List<ReflectionDto> reflections =
        reflectionQueryRepository.getReflectionsByExperience(
            experienceId, cursorConverter.decode(pagination.cursor()), pagination.size() + 1);
    log.debug("Fetched {} reflections for experience {}", reflections.size(), experienceId);

    return new GetReflectionsResponse(
        mapToCursorPaginationResponse(reflections, pagination.size()));
  }

  private CursorPaginationResponse<GetReflectionsResponse.Reflection> mapToCursorPaginationResponse(
      List<ReflectionDto> reflections, int size) {
    boolean hasNext = reflections.size() > size;

    Cursor next = getNextCursor(reflections.subList(0, Math.min(size, reflections.size())));
    String nextCursor = next == null ? null : cursorConverter.encode(next);

    var content =
        reflections.stream().limit(size).map(reflectionMapper::toReflectionResponse).toList();

    return CursorPaginationResponse.<GetReflectionsResponse.Reflection>builder()
        .content(content)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  private Cursor getNextCursor(List<ReflectionDto> reflections) {
    if (reflections.isEmpty()) {
      return null;
    }

    ReflectionDto lastReflection = reflections.get(reflections.size() - 1);
    return Cursor.builder()
        .score(0L)
        .createdAt(lastReflection.createdAt().toInstant(ZoneOffset.UTC))
        .updatedAt(lastReflection.updatedAt().toInstant(ZoneOffset.UTC))
        .id(lastReflection.id())
        .build();
  }
}
