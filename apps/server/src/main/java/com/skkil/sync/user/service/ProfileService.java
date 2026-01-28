package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

  private final UserRepository userRepository;

  public ProfileService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void updateProfile(Long userId, UpdateProfileRequest request) {
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (request.name() != null) {
      user.setFullName(request.name());
    }
  }
}
