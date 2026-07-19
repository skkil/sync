package com.skkil.sync.post.service;

import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import com.skkil.sync.post.event.PostPublishedEvent;
import com.skkil.sync.post.mapper.PostActivityMapper;
import com.skkil.sync.post.repository.PostActivityRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class PostActivityService {

  private final PostActivityRepository postActivityRepository;
  private final PostRepository postRepository;
  private final UserDomainService userDomainService;
  private final PostActivityMapper postActivityMapper;

  public PostActivityService(
      PostActivityRepository postActivityRepository,
      PostRepository postRepository,
      UserDomainService userDomainService,
      PostActivityMapper postActivityMapper) {
    this.postActivityRepository = postActivityRepository;
    this.postRepository = postRepository;
    this.userDomainService = userDomainService;
    this.postActivityMapper = postActivityMapper;
  }

  @Transactional(readOnly = true)
  public GetPostActivitiesResponse getPostActivities(String handle, Integer year) {
    User user = userDomainService.getUserByHandle(handle);

    var activities =
        postActivityRepository
            .findAllByUserAndBetweenYears(
                user, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
            .stream()
            .map(postActivityMapper::toActivity)
            .toList();

    return new GetPostActivitiesResponse(activities);
  }

  @Async
  @TransactionalEventListener
  public void recordPublishActivity(PostPublishedEvent event) {
    log.debug("Recording publish activity for post {}", event.getPostId());

    postRepository.incrementActivityCount(event.getAuthorId(), event.getPublishedDate());
  }
}
