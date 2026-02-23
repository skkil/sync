package com.skkil.sync.user.repository;

import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.model.UserOAuth2Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OAuth2AccountRepository extends JpaRepository<UserOAuth2Account, Long> {

  List<UserOAuth2Account> findByUserId(Long userId);

  @Query(
      """
      SELECT a FROM UserOAuth2Account a WHERE a.user.id = :userId AND a.oAuth2Provider = :provider
      """)
  Optional<UserOAuth2Account> findByUserAndProvider(Long userId, OAuth2Provider provider);
}
