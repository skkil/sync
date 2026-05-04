package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.Reflection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

  Optional<Reflection> findBySlug(String slug);
}
