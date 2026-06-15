package com.skkil.sync.post.service;

import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import com.skkil.sync.post.repository.PostActivityRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostActivityService {

  private final PostActivityRepository postActivityRepository;
  private final UserDomainService userDomainService;

  public PostActivityService(
      PostActivityRepository postActivityRepository, UserDomainService userDomainService) {
    this.postActivityRepository = postActivityRepository;
    this.userDomainService = userDomainService;
  }

  @Transactional(readOnly = true)
  public GetPostActivitiesResponse getPostActivities(String handle, Integer year) {
    User user = userDomainService.getUserByHandle(handle);

    var activities =
        postActivityRepository
            .findAllByUserAndBetweenYears(
                user, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
            .stream()
            .map(
                activity ->
                    GetPostActivitiesResponse.Activity.builder()
                        .date(activity.getDate())
                        .count(activity.getCount())
                        .build())
            .toList();

    return new GetPostActivitiesResponse(activities);
  }
}
