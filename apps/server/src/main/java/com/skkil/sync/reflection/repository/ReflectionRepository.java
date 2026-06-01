package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.Reflection;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

  Optional<Reflection> findBySlug(String slug);

  @Query(
      """
      SELECT r
      FROM Reflection r
      JOIN FETCH r.author
      LEFT JOIN FETCH r.tags rt
      LEFT JOIN FETCH rt.tag
      WHERE r.id = :id
      """)
  Optional<Reflection> findByIdWithAuthorAndTags(Long id);

  @Modifying
  @Query(
      value =
          "INSERT INTO reflection_activities(user_id, date, count) VALUES (:userId, :date, 1) "
              + "ON CONFLICT (user_id, date) DO UPDATE SET count = reflection_activities.count + 1",
      nativeQuery = true)
  void incrementActivityCount(Long userId, LocalDate date);
}
