package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.summary.TagSummary;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagFollowRelationship;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagFollowServiceTests {

  @Mock private TagRepository tagRepository;

  @Mock private UserRepository userRepository;

  @Mock private UserDomainService userDomainService;

  @Mock private TagFollowRelationshipRepository tagFollowRelationshipRepository;

  @Mock private TagMapper tagMapper;

  @Mock private PaginationService paginationService;

  @InjectMocks private TagFollowService tagFollowService;

  @Test
  @DisplayName("[followTag] 팔로우에 성공하면 태그의 followerCount를 1 증가")
  void followTag_success_incrementsFollowerCount() {
    Long followerId = 1L;
    Long tagId = 2L;
    Tag tag = Tag.builder().name("java").build();
    User follower = User.builder().build();

    when(tagRepository.findByIdAndProjectIsNull(tagId)).thenReturn(Optional.of(tag));
    when(tagFollowRelationshipRepository.existsByFollowerAndTag(eq(followerId), any()))
        .thenReturn(false);
    when(userRepository.getReferenceById(followerId)).thenReturn(follower);

    tagFollowService.followTag(followerId, tagId);

    verify(tagRepository, times(1)).incrementFollowerCount(tag);
  }

  @Test
  @DisplayName("[followTag] 이미 팔로우 중이면 followerCount를 증가시키지 않음")
  void followTag_alreadyFollowing_doesNotIncrementFollowerCount() {
    Long followerId = 1L;
    Long tagId = 2L;
    Tag tag = Tag.builder().name("java").build();

    when(tagRepository.findByIdAndProjectIsNull(tagId)).thenReturn(Optional.of(tag));
    when(tagFollowRelationshipRepository.existsByFollowerAndTag(eq(followerId), any()))
        .thenReturn(true);

    tagFollowService.followTag(followerId, tagId);

    verify(tagRepository, never()).incrementFollowerCount(any(Tag.class));
  }

  @Test
  @DisplayName("[unfollowTag] 언팔로우에 성공하면 태그의 followerCount를 1 감소")
  void unfollowTag_success_decrementsFollowerCount() {
    Long followerId = 1L;
    Long tagId = 2L;
    Tag tag = Tag.builder().name("java").build();

    when(tagFollowRelationshipRepository.deleteByFollowerAndTag(followerId, tagId)).thenReturn(1);
    when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

    tagFollowService.unfollowTag(followerId, tagId);

    verify(tagRepository, times(1)).decrementFollowerCount(tag);
  }

  @Test
  @DisplayName("[unfollowTag] 팔로우 중이 아니었다면 followerCount를 감소시키지 않음")
  void unfollowTag_notFollowing_doesNotDecrementFollowerCount() {
    Long followerId = 1L;
    Long tagId = 2L;

    when(tagFollowRelationshipRepository.deleteByFollowerAndTag(followerId, tagId)).thenReturn(0);

    tagFollowService.unfollowTag(followerId, tagId);

    verify(tagRepository, never()).decrementFollowerCount(any(Tag.class));
    verify(tagRepository, never()).findById(any());
  }

  @Test
  @DisplayName("[getFollowedTags] 사용자가 팔로우한 태그 목록을 TagSummary로 반환")
  void getFollowedTags_returnsTagSummaries() {
    String handle = "user-handle";
    User user = new User(1L);
    Tag tag = Tag.builder().name("java").build();
    TagFollowRelationship relationship =
        TagFollowRelationship.builder().follower(user).tag(tag).build();
    TagSummary summary =
        TagSummary.builder().id(null).name("java").postCount(0L).followerCount(0L).build();
    OffsetPaginationRequest pagination = new OffsetPaginationRequest(0, 10);
    OffsetPaginationResponse<TagFollowRelationship> page =
        new OffsetPaginationResponse<>(
            OffsetPaginationResponse.PageInfo.builder()
                .page(0)
                .size(10)
                .hasNextPage(false)
                .hasPreviousPage(false)
                .build(),
            List.of(relationship));

    when(userDomainService.getUserByHandle(handle)).thenReturn(user);
    when(tagFollowRelationshipRepository.findTagIdsByFollowerId(1L)).thenReturn(Set.of());
    doReturn(page).when(paginationService).paginate(any(), eq(pagination));
    when(tagMapper.toTagSummary(tag, Set.of())).thenReturn(summary);

    var response = tagFollowService.getFollowedTags(handle, pagination);

    assertThat(response.tags().content()).containsExactly(summary);
  }
}
