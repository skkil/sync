package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.request.UpdateUserPreferencesRequest;
import com.skkil.sync.user.dto.response.GetUserPreferencesResponse;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserPreferences;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferencesService {

  private final UserRepository userRepository;

  public UserPreferencesService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public GetUserPreferencesResponse getUserPreferences(Long userId) {
    User user =
        userRepository
            .findByIdWithPreferences(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

    UserPreferences preferences = user.getPreferences();
    if (preferences == null) {
      preferences = new UserPreferences();
    }

    return new GetUserPreferencesResponse(preferences.getTheme());
  }

  @Transactional
  public void updateUserPreferences(Long userId, UpdateUserPreferencesRequest request) {
    User user =
        userRepository
            .findByIdWithPreferences(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

    UserPreferences preferences = user.getPreferences();
    if (preferences == null) {
      preferences = new UserPreferences();
      preferences.setUser(user);
      user.setPreferences(preferences);
    }

    if (request.theme() != null) {
      preferences.updateTheme(request.theme());
    }

    userRepository.save(user);
  }
}
