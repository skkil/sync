package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

  @Query("SELECT p FROM Provider p WHERE p.type = :type AND p.verifiedBy IS NOT NULL")
  public List<Provider> findByTypeAndVerified(ProviderType type);

  @Query("SELECT p FROM Provider p WHERE p.verifiedBy IS NULL")
  public Page<Provider> findUnverifiedProviders(Pageable pageable);

  @Query(
      """
      SELECT
      p FROM Provider p
      WHERE
      p.name LIKE %:query% AND p.verifiedBy IS NOT NULL AND p.type = :type
      """)
  public Page<Provider> searchProviders(ProviderType type, String query, Pageable pageable);
}
