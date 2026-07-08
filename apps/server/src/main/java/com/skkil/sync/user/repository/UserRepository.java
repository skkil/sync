package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  @Modifying
  @Query(
      value = "UPDATE users SET follower_count = follower_count + 1 WHERE id = :userId",
      nativeQuery = true)
  void incrementFollowerCount(Long userId);

  @Modifying
  @Query(
      value = "UPDATE users SET following_count = following_count + 1 WHERE id = :userId",
      nativeQuery = true)
  void incrementFollowingCount(Long userId);

  @Modifying
  @Query(
      value =
          "UPDATE users SET follower_count = GREATEST(follower_count - 1, 0) WHERE id = :userId",
      nativeQuery = true)
  void decrementFollowerCount(Long userId);

  @Modifying
  @Query(
      value =
          "UPDATE users SET following_count = GREATEST(following_count - 1, 0) WHERE id = :userId",
      nativeQuery = true)
  void decrementFollowingCount(Long userId);

  boolean existsByHandle(String handle);

  Optional<User> findByEmail(String email);

  Optional<User> findByHandle(String handle);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.preferences WHERE u.id = :id")
  Optional<User> findByIdWithPreferences(Long id);

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

  @Query(
      """
      SELECT u FROM User u
      WHERE u.deletedAt IS NULL AND (
        LOWER(u.handle) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
      )
      LIMIT 10
      """)
  List<User> searchUsers(String query);
}
