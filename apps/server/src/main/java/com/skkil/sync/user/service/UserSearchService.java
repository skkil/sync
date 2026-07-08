package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.response.SearchUsersResponse;
import com.skkil.sync.user.mapper.UserAssembler;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSearchService {

  private final UserRepository userRepository;
  private final UserAssembler userAssembler;

  public UserSearchService(UserRepository userRepository, UserAssembler userAssembler) {
    this.userRepository = userRepository;
    this.userAssembler = userAssembler;
  }

  @Transactional(readOnly = true)
  public SearchUsersResponse searchUsers(String query) {
    var users = userRepository.searchUsers(query);

    return new SearchUsersResponse(userAssembler.toUserSummariesInOrder(users));
  }
}
