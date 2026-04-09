package com.skkil.sync.provider.contest.repository;

import com.skkil.sync.provider.contest.model.Contest;
import com.skkil.sync.provider.contest.model.ContestOccurrence;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestOccurrenceRepository extends JpaRepository<ContestOccurrence, Long> {

  Optional<ContestOccurrence> findByIdAndContestId(Long occurrenceId, Long contestId);

  Page<ContestOccurrence> findByContest(Contest contest, Pageable pageable);
}
