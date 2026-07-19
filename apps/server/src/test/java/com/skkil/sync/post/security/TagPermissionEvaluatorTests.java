package com.skkil.sync.post.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.repository.TeammateRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagPermissionEvaluatorTests {

  @Mock private TagRepository tagRepository;

  @Mock private TeammateRepository teammateRepository;

  @InjectMocks private TagPermissionEvaluator permissionEvaluator;

  @Test
  @DisplayName("[hasPermission] 공개 프로젝트의 태그는 팀원이 아니어도 READ 권한을 가짐")
  void hasPermission_publicProjectTag_read_returnsTrueWithoutMembership() {
    Long tagId = 1L;
    Project project =
        Project.builder().handle("public-project").name("Public").isPublic(true).build();
    Tag tag = Tag.builder().name("java").project(project).build();

    when(tagRepository.findByIdWithProject(tagId)).thenReturn(Optional.of(tag));

    assertThat(permissionEvaluator.hasPermission(null, tagId, PermissionOperation.READ)).isTrue();
  }

  @Test
  @DisplayName("[hasPermission] 비공개 프로젝트의 태그는 팀원이 아니면 READ 권한이 없음")
  void hasPermission_privateProjectTag_read_nonTeammate_returnsFalse() {
    Long tagId = 1L;
    Project project =
        Project.builder().handle("private-project").name("Private").isPublic(false).build();
    Tag tag = Tag.builder().name("java").project(project).build();
    AuthenticatedUser user = new AuthenticatedUser(2L);

    when(tagRepository.findByIdWithProject(tagId)).thenReturn(Optional.of(tag));
    when(teammateRepository.findByProjectHandleAndUserId("private-project", 2L))
        .thenReturn(Optional.empty());

    assertThat(permissionEvaluator.hasPermission(user, tagId, PermissionOperation.READ)).isFalse();
  }
}
