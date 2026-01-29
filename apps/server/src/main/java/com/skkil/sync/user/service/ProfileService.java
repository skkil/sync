package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {

  private final UserRepository userRepository;

  public ProfileService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public GetProfileResponse getProfile(Long userId) {
    log.debug("Fetching profile for userId: {}", userId);
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return GetProfileResponse.builder()
        .userId(user.getId().toString())
        .name(user.getFullName())
        .email(user.getEmail())
        .bio(user.getBio())
        .build();
  }
}
