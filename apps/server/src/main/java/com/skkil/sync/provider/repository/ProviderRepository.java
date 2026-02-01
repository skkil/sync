package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

  public List<Provider> findByType(ProviderType type);
}
