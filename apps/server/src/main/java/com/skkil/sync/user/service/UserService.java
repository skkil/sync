package com.skkil.sync.user.service;

import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUserReference(Long userId) {
    return userRepository.getReferenceById(userId);
  }
}
