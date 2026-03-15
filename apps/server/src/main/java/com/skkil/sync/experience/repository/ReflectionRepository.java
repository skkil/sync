package com.skkil.sync.experience.repository;

import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.model.Reflection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

  List<Reflection> findByExperience(Experience experience);

  Optional<Reflection> findByIdAndExperienceId(Long id, Long experienceId);
}
