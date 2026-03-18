package com.skkil.sync.experience.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.experience.dto.request.UpdateReflectionRequest;
import com.skkil.sync.experience.exception.ReflectionNotFoundException;
import com.skkil.sync.experience.model.Reflection;
import com.skkil.sync.experience.repository.ExperienceRepository;
import com.skkil.sync.experience.repository.ReflectionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReflectionServiceTests {

  @Mock private ExperienceRepository experienceRepository;
  @Mock private ReflectionRepository reflectionRepository;

  private ReflectionService reflectionService;

  @BeforeEach
  void setUp() {
    reflectionService = new ReflectionService(experienceRepository, reflectionRepository);
  }

  @Test
  void updateReflection_updatesContent() {
    Reflection reflection = Reflection.builder().content("before").build();

    when(reflectionRepository.findByIdAndExperienceId(2L, 1L)).thenReturn(Optional.of(reflection));

    reflectionService.updateReflection(1L, 2L, new UpdateReflectionRequest("after"));

    assertThat(reflection.getContent()).isEqualTo("after");
  }

  @Test
  void updateReflection_reflectionNotFound_throwsException() {
    when(reflectionRepository.findByIdAndExperienceId(2L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> reflectionService.updateReflection(1L, 2L, new UpdateReflectionRequest("after")))
        .isInstanceOf(ReflectionNotFoundException.class);
  }

  @Test
  void deleteReflection_deletesReflection() {
    Reflection reflection = Reflection.builder().content("content").build();

    when(reflectionRepository.findByIdAndExperienceId(2L, 1L)).thenReturn(Optional.of(reflection));

    reflectionService.deleteReflection(1L, 2L);

    verify(reflectionRepository).delete(reflection);
  }

  @Test
  void deleteReflection_reflectionNotFound_throwsException() {
    when(reflectionRepository.findByIdAndExperienceId(2L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reflectionService.deleteReflection(1L, 2L))
        .isInstanceOf(ReflectionNotFoundException.class);
  }
}
