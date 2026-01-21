package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.oAuth2Accounts WHERE u.email = :email")
  Optional<User> findByEmailWithOAuthAccounts(String email);
}
