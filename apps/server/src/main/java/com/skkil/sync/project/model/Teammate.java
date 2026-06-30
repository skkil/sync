package com.skkil.sync.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
    name = "teammates",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "user_id"})})
@Getter
public class Teammate extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "is_owner", nullable = false)
  private Boolean isOwner = false;

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role = Role.MEMBER;

  protected Teammate() {}

  protected Teammate(Project project, User user) {
    this.project = project;
    this.user = user;
  }

  public static Teammate owner(Project project, User user) {
    Teammate teammate = new Teammate(project, user);
    teammate.role = Role.ADMIN;
    teammate.isOwner = true;
    return teammate;
  }

  public static Teammate member(Project project, User user) {
    return new Teammate(project, user);
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
