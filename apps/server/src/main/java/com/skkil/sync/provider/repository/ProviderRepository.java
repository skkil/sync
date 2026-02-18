package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

  @Query(
      """
      SELECT p FROM Provider p
      WHERE (:query IS NOT NULL AND p.name LIKE :query%)
        AND p.type IN :types
        AND (p.id > :cursor OR :cursor IS NULL)
        AND p.verifiedBy IS NOT NULL
      ORDER BY p.id
      """)
  public Page<Provider> searchByTypeAndVerified(
      String query, List<ProviderType> types, Long cursor, Pageable pageable);

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
