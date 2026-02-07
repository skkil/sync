package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

  Page<Provider> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String nameKeyword, String descriptionKeyword, Pageable pageable);
}
