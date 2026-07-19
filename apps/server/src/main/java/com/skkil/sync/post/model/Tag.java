package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.project.model.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "tags", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"}))
@Getter
public class Tag extends BaseEntity {

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "post_count", nullable = false)
  private Long postCount;

  @Column(name = "follower_count", nullable = false)
  private Long followerCount;

  @Column(name = "verified", nullable = false)
  private boolean verified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  protected Tag() {}

  @Builder
  public Tag(String name, String description, Project project) {
    this.name = name;
    this.description = description;
    this.postCount = 0L;
    this.followerCount = 0L;
    this.project = project;
  }

  public void verify() {
    this.verified = true;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void updateName(String name) {
    this.name = name;
  }
}
