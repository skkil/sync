package com.skkil.sync.project.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.media.repository.MediaRepository;
import com.skkil.sync.post.repository.PostTagRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.repository.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectDeletionServiceUnitTests {

  @Mock private ProjectRepository projectRepository;

  @Mock private PostTagRepository postTagRepository;

  @Mock private TagRepository tagRepository;

  @Mock private MediaRepository mediaRepository;

  @Mock private Project project;

  private ProjectDeletionService projectDeletionService;

  @BeforeEach
  void setUp() {
    projectDeletionService =
        new ProjectDeletionService(
            projectRepository, postTagRepository, tagRepository, mediaRepository);
  }

  @Test
  @DisplayName("[delete] 영향받은 글로벌 태그를 프로젝트 삭제 후 한 번에 재집계한다")
  void delete_affectedGlobalTags_recomputesPostCountsOnceAfterFlush() {
    Long projectId = 1L;
    List<Long> affectedGlobalTagIds = List.of(11L, 12L);
    when(project.getId()).thenReturn(projectId);
    when(postTagRepository.findGlobalTagIdsByProjectId(projectId)).thenReturn(affectedGlobalTagIds);

    projectDeletionService.delete(project);

    InOrder inOrder = inOrder(postTagRepository, mediaRepository, projectRepository, tagRepository);
    inOrder.verify(postTagRepository).findGlobalTagIdsByProjectId(projectId);
    inOrder.verify(mediaRepository).markProjectMediaDeleted(projectId);
    inOrder.verify(projectRepository).delete(project);
    inOrder.verify(projectRepository).flush();
    inOrder.verify(tagRepository).recomputePostCounts(affectedGlobalTagIds);
    verify(tagRepository, never()).recomputeCounts(anyLong());
  }

  @Test
  @DisplayName("[delete] 영향받은 글로벌 태그가 없으면 일괄 재집계를 생략한다")
  void delete_noAffectedGlobalTags_skipsPostCountRecomputation() {
    Long projectId = 1L;
    when(project.getId()).thenReturn(projectId);
    when(postTagRepository.findGlobalTagIdsByProjectId(projectId)).thenReturn(List.of());

    projectDeletionService.delete(project);

    verify(tagRepository, never()).recomputePostCounts(anyList());
    verify(tagRepository, never()).recomputeCounts(anyLong());
  }
}
