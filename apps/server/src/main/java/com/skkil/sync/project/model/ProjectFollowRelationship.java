package com.skkil.sync.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "project_follow_relationships",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_project_follow_relationships_follower_project",
          columnNames = {"follower_id", "project_id"})
    })
@Getter
public class ProjectFollowRelationship extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  protected ProjectFollowRelationship() {}

  @Builder
  public ProjectFollowRelationship(User follower, Project project) {
    this.follower = follower;
    this.project = project;
  }
}
