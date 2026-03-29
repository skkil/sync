package com.skkil.sync.provider.company.repository;

import com.skkil.sync.provider.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}
