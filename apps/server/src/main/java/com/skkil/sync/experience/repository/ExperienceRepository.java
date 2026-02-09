package com.skkil.sync.experience.repository;

import com.skkil.sync.experience.model.Experience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

  @Query(
      """
      SELECT e FROM Experience e
      JOIN FETCH e.provider
      WHERE e.user.id = :userId
      """)
  public List<Experience> findByUserWithProvider(Long userId);

  @Query(
      """
      SELECT e FROM Experience e
      JOIN FETCH e.provider
      WHERE e.user.id = :userId AND e.type = :type
      """)
  public List<Experience> findByUserIdAndType(Long userId, String type);
}
