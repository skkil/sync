package com.skkil.sync.common.seeder;

import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import org.springframework.stereotype.Component;

@Component
class UserSeeder {

  private final AuthService authService;
  private final UserRepository userRepository;

  UserSeeder(AuthService authService, UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  User seed(
      String email,
      String password,
      String handle,
      String fullName,
      String profession,
      String bio) {
    User user = authService.registerUser(new RegisterRequest(email, password));
    user.updateHandle(handle);
    user.updateFields(fullName, profession, bio);
    user.onboard();
    return userRepository.save(user);
  }
}
