package com.skkil.sync.user.mapper;

import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final MediaDomainService mediaDomainService;

  public UserAssembler(
      UserMapper userMapper, UserRepository userRepository, MediaDomainService mediaDomainService) {
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.mediaDomainService = mediaDomainService;
  }

  public UserSummary toUserSummary(Long userId) {
    return toUserSummaries(List.of(userId)).get(userId);
  }

  public Map<Long, UserSummary> toUserSummaries(List<Long> userIds) {
    List<User> users = userRepository.findAllById(userIds);
    Map<Long, URL> profileImageUrls =
        mediaDomainService.generatePublicGetUrls(users, User::getProfileImage);

    return users.stream()
        .collect(
            Collectors.toMap(
                User::getId, user -> toUserSummary(user, profileImageUrls), (a, b) -> a));
  }

  public List<UserSummary> toUserSummariesInOrder(List<User> users) {
    Map<Long, URL> profileImageUrls =
        mediaDomainService.generatePublicGetUrls(users, User::getProfileImage);

    return users.stream().map(user -> toUserSummary(user, profileImageUrls)).toList();
  }

  private UserSummary toUserSummary(User user, Map<Long, URL> profileImageUrls) {
    var profileImage = user.getProfileImage();
    URL url = profileImage != null ? profileImageUrls.get(profileImage.getId()) : null;
    return userMapper.toUserSummary(user, url != null ? url.toString() : null);
  }
}
