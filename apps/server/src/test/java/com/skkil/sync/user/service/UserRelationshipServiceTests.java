package com.skkil.sync.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.user.event.UserFollowedEvent;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import com.skkil.sync.user.repository.UserFollowRelationshipRepository;
import com.skkil.sync.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserRelationshipServiceTests {

  @Mock private UserRepository userRepository;
  @Mock private UserFollowRelationshipRepository userFollowRelationshipRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private UserRelationshipService userRelationshipService;

  @Test
  @DisplayName("[followUser] 새 팔로우 관계가 생성되면 UserFollowedEvent 발행")
  void followUser_newRelationship_publishUserFollowedEvent() {
    Long followerId = 1L;
    Long followeeId = 2L;

    when(userFollowRelationshipRepository.existsByFollowerAndFollowee(followerId, followeeId))
        .thenReturn(false);
    when(userRepository.getReferenceById(followerId)).thenReturn(new User(followerId));
    when(userRepository.getReferenceById(followeeId)).thenReturn(new User(followeeId));

    userRelationshipService.followUser(followerId, followeeId);

    verify(userFollowRelationshipRepository).save(any(UserFollowRelationship.class));

    ArgumentCaptor<UserFollowedEvent> eventCaptor =
        ArgumentCaptor.forClass(UserFollowedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    UserFollowedEvent event = eventCaptor.getValue();
    assertThat(event.getFollowerId()).isEqualTo(followerId);
    assertThat(event.getFolloweeId()).isEqualTo(followeeId);
  }

  @Test
  @DisplayName("[followUser] 이미 팔로우 중이면 관계 저장과 이벤트 발행 생략")
  void followUser_alreadyFollowing_skipSaveAndEvent() {
    Long followerId = 1L;
    Long followeeId = 2L;

    when(userFollowRelationshipRepository.existsByFollowerAndFollowee(followerId, followeeId))
        .thenReturn(true);

    userRelationshipService.followUser(followerId, followeeId);

    verify(userFollowRelationshipRepository, never()).save(any(UserFollowRelationship.class));
    verify(eventPublisher, never()).publishEvent(any());
  }
}
