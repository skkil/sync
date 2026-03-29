package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByHandle(String handle);

  Optional<User> findByEmail(String email);

  Optional<User> findByHandle(String handle);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.oAuth2Accounts WHERE u.email = :email")
  Optional<User> findByEmailWithOAuthAccounts(String email);

  @Query(
      """
      SELECT u FROM User u
      WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')) AND u.deletedAt IS NULL
      """)
  Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

  @Query(
      """
      SELECT COUNT(u) FROM User u
      WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')) AND u.deletedAt IS NULL
      """)
  long countByFullNameContainingIgnoreCase(String fullName);
}
