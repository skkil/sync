package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTemplateRepository extends JpaRepository<PostTemplate, Long> {

  List<PostTemplate> findByProjectIdOrderByNameAsc(Long projectId);

  Optional<PostTemplate> findByProjectIdAndExternalId(Long projectId, String externalId);

  boolean existsByProjectIdAndName(Long projectId, String name);

  long countByProjectId(Long projectId);
}
