package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.dto.response.GetReflectionActivitiesResponse;
import com.skkil.sync.reflection.repository.ReflectionActivityRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionActivityService {

  private final ReflectionActivityRepository reflectionActivityRepository;
  private final UserDomainService userDomainService;

  public ReflectionActivityService(
      ReflectionActivityRepository reflectionActivityRepository,
      UserDomainService userDomainService) {
    this.reflectionActivityRepository = reflectionActivityRepository;
    this.userDomainService = userDomainService;
  }

  @Transactional(readOnly = true)
  public GetReflectionActivitiesResponse getReflectionActivities(String handle, Integer year) {
    User user = userDomainService.getUserByHandle(handle);

    var activities =
        reflectionActivityRepository.findAllByUserAndYear(user, year).stream()
            .map(
                activity ->
                    GetReflectionActivitiesResponse.Activity.builder()
                        .date(activity.getDate())
                        .count(activity.getCount())
                        .build())
            .toList();

    return new GetReflectionActivitiesResponse(activities);
  }
}
