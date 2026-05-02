package com.skkil.sync.provider.contest.repository;

import com.skkil.sync.provider.contest.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Long> {}
