package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.Post;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findBySlug(String slug);

  @Modifying
  @Query(
      value =
          "INSERT INTO post_activities(user_id, date, count) VALUES (:userId, :date, 1) "
              + "ON CONFLICT (user_id, date) DO UPDATE SET count = post_activities.count + 1",
      nativeQuery = true)
  void incrementActivityCount(Long userId, LocalDate date);
}
