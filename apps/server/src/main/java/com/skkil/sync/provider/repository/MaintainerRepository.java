package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.model.Maintainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintainerRepository extends JpaRepository<Maintainer, Long> {

  boolean existsByProviderIdAndUserId(Long providerId, Long userId);
}
