package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.response.GetFollowedTagsResponse;
import com.skkil.sync.post.exception.TagNotFoundException;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagFollowRelationship;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TagFollowService {

  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final UserDomainService userDomainService;
  private final TagFollowRelationshipRepository tagFollowRelationshipRepository;
  private final TagMapper tagMapper;
  private final PaginationService paginationService;

  public TagFollowService(
      TagRepository tagRepository,
      UserRepository userRepository,
      UserDomainService userDomainService,
      TagFollowRelationshipRepository tagFollowRelationshipRepository,
      TagMapper tagMapper,
      PaginationService paginationService) {
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
    this.tagFollowRelationshipRepository = tagFollowRelationshipRepository;
    this.tagMapper = tagMapper;
    this.paginationService = paginationService;
  }

  @Transactional
  public void followTag(Long followerId, Long tagId) {
    log.debug("User {} is attempting to follow tag {}", followerId, tagId);

    Tag tag =
        tagRepository
            .findByIdAndProjectIsNull(tagId)
            .orElseThrow(() -> new TagNotFoundException(tagId));

    if (tagFollowRelationshipRepository.existsByFollowerAndTag(followerId, tag.getId())) {
      log.debug("User {} is already following tag {}", followerId, tagId);
      return;
    }

    User follower = userRepository.getReferenceById(followerId);
    var relationship = TagFollowRelationship.builder().follower(follower).tag(tag).build();

    try {
      // saveAndFlush is required here (rather than save) so that a unique-constraint
      // violation from a concurrent follow request surfaces inside this try block
      // instead of at transaction commit, after the method has already returned.
      tagFollowRelationshipRepository.saveAndFlush(relationship);
      tagRepository.incrementFollowerCount(tag);
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "User {} was concurrently followed to tag {}, ignoring duplicate", followerId, tagId);
    }
  }

  @Transactional
  public void unfollowTag(Long followerId, Long tagId) {
    log.debug("User {} is attempting to unfollow tag {}", followerId, tagId);

    int deleted = tagFollowRelationshipRepository.deleteByFollowerAndTag(followerId, tagId);
    if (deleted > 0) {
      tagRepository.findById(tagId).ifPresent(tagRepository::decrementFollowerCount);
    }
  }

  @Transactional(readOnly = true)
  public GetFollowedTagsResponse getFollowedTags(
      String userHandle, OffsetPaginationRequest pagination) {
    log.debug("Retrieving tags followed by user {}", userHandle);

    User user = userDomainService.getUserByHandle(userHandle);
    Set<Long> followedTagIds = tagFollowRelationshipRepository.findTagIdsByFollowerId(user.getId());

    var page =
        paginationService.paginate(
            pageable -> tagFollowRelationshipRepository.findByFollowerId(user.getId(), pageable),
            pagination);

    return new GetFollowedTagsResponse(
        page.map(relationship -> tagMapper.toTagSummary(relationship.getTag(), followedTagIds)));
  }
}
