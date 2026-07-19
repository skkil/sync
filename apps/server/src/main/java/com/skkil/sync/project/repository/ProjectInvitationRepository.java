package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.InvitationStatus;
import com.skkil.sync.project.model.ProjectInvitation;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long> {

  @EntityGraph(attributePaths = {"project", "inviter", "invitee"})
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ProjectInvitation> findByToken(String token);

  @EntityGraph(attributePaths = {"invitee", "inviter"})
  List<ProjectInvitation> findByProjectIdAndStatus(Long projectId, InvitationStatus status);

  @EntityGraph(attributePaths = {"project", "inviter"})
  List<ProjectInvitation> findByInviteeIdAndStatus(Long inviteeId, InvitationStatus status);

  boolean existsByProjectIdAndInviteeIdAndStatus(
      Long projectId, Long inviteeId, InvitationStatus status);
}
