package com.skkil.sync.provider.contest.repository;

import com.skkil.sync.provider.contest.model.Contest;
import com.skkil.sync.provider.contest.model.ContestOccurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestOccurrenceRepository extends JpaRepository<ContestOccurrence, Long> {

  Page<ContestOccurrence> findByContest(Contest contest, Pageable pageable);
}
