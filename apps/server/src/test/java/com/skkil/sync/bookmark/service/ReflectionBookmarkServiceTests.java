package com.skkil.sync.bookmark.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.skkil.sync.bookmark.repository.ReflectionBookmarkRepository;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.service.ReflectionDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReflectionBookmarkServiceTests {

  @Mock private ReflectionBookmarkRepository reflectionBookmarkRepository;
  @Mock private ReflectionDomainService reflectionDomainService;

  @InjectMocks private ReflectionBookmarkService reflectionBookmarkService;

  @Test
  @DisplayName("[bookmarkReflection] 회고 북마크를 생성함")
  void bookmarkReflection_insertsBookmark() {
    Long userId = 1L;
    Long reflectionId = 2L;

    reflectionBookmarkService.bookmarkReflection(userId, reflectionId);

    verify(reflectionDomainService).getReflection(reflectionId);
    verify(reflectionBookmarkRepository).insertIfAbsent(userId, reflectionId);
  }

  @Test
  @DisplayName("[bookmarkReflection] 존재하지 않는 회고를 북마크하려는 경우 예외 발생")
  void bookmarkReflection_reflectionNotFound_throwsException() {
    Long userId = 1L;
    Long reflectionId = 2L;

    doThrow(new ReflectionNotFoundException(reflectionId))
        .when(reflectionDomainService)
        .getReflection(reflectionId);

    assertThatThrownBy(() -> reflectionBookmarkService.bookmarkReflection(userId, reflectionId))
        .isInstanceOf(ReflectionNotFoundException.class);

    verify(reflectionBookmarkRepository, never()).insertIfAbsent(userId, reflectionId);
  }

  @Test
  @DisplayName("[unbookmarkReflection] 회고 북마크를 삭제함")
  void unbookmarkReflection_deletesBookmark() {
    Long userId = 1L;
    Long reflectionId = 2L;

    reflectionBookmarkService.unbookmarkReflection(userId, reflectionId);

    verify(reflectionBookmarkRepository).deleteByUser_IdAndReflection_Id(userId, reflectionId);
    verifyNoInteractions(reflectionDomainService);
  }
}
