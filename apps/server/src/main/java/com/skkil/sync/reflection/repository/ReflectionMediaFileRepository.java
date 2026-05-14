package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.ReflectionMediaFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReflectionMediaFileRepository extends JpaRepository<ReflectionMediaFile, Long> {

  List<ReflectionMediaFile> findAllByReflectionIdOrderBySortOrderAsc(Long reflectionId);
}
