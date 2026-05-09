package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.ReflectionActivity;
import com.skkil.sync.user.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReflectionActivityRepository extends JpaRepository<ReflectionActivity, Long> {

  @Query(
      """
      SELECT
        a FROM ReflectionActivity a
      WHERE
        a.user = :user AND a.date BETWEEN :start AND :end
      """)
  List<ReflectionActivity> findAllByUserAndBetweenYears(User user, LocalDate start, LocalDate end);
}
