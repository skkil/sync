package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostActivity;
import com.skkil.sync.user.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostActivityRepository extends JpaRepository<PostActivity, Long> {

  @Query(
      """
      SELECT
        a FROM PostActivity a
      WHERE
        a.user = :user AND a.date BETWEEN :start AND :end
      """)
  List<PostActivity> findAllByUserAndBetweenYears(User user, LocalDate start, LocalDate end);
}
