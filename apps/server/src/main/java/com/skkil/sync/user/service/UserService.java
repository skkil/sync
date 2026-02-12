package com.skkil.sync.user.service;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public AuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

    return new AuthenticatedUser(
        user.getId(),
        user.getFullName(),
        user.getEmail(),
        user.getHashedPassword(),
        user.getRole());
  }

  public User getUserReference(Long userId) {
    return userRepository.getReferenceById(userId);
  }
}
