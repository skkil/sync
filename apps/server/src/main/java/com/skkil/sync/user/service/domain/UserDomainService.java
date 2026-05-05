package com.skkil.sync.user.service.domain;

import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDomainService {

  private final UserRepository userRepository;

  public UserDomainService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUserReference(Long userId) {
    return userRepository.getReferenceById(userId);
  }

  @Transactional(readOnly = true)
  public User getUserByHandle(String handle) {
    return userRepository.findByHandle(handle).orElseThrow(() -> new UserNotFoundException(handle));
  }
}
