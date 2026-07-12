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

  @Column(name = "verified", nullable = false)
  private boolean verified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  protected Tag() {}

  @Builder
  public Tag(String name, Project project) {
    this.name = name;
    this.description = "";
    this.postCount = 0L;
    this.project = project;
  }

  public void verify() {
    this.verified = true;
  }
}
