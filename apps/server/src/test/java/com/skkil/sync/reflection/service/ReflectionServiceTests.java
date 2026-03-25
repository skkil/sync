package com.skkil.sync.reflection.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.reflection.snippets.UpdateReflectionRequestSnippets;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReflectionServiceTests {

  @Mock private ReflectionRepository reflectionRepository;

  @InjectMocks private ReflectionService reflectionService;

  @Test
  @DisplayName("[updateReflection] 존재하지 않는 회고를 수정하려는 경우 ReflectionNotFoundException 예외 발생")
  void updateReflection_reflectionNotFound_throwsException() {
    Long reflectionId = 1L;
    UpdateReflectionRequest request = UpdateReflectionRequestSnippets.getUpdateReflectionRequest();

    when(reflectionRepository.findById(reflectionId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reflectionService.updateReflection(reflectionId, request))
        .isInstanceOf(ReflectionNotFoundException.class);
  }

  @Test
  @DisplayName("[deleteReflection] 존재하지 않는 회고를 삭제하려는 경우 ReflectionNotFoundException 예외 발생")
  void deleteReflection_reflectionNotFound_throwsException() {
    Long reflectionId = 1L;

    when(reflectionRepository.existsById(reflectionId)).thenReturn(false);

    assertThatThrownBy(() -> reflectionService.deleteReflection(reflectionId))
        .isInstanceOf(ReflectionNotFoundException.class);
  }
}
