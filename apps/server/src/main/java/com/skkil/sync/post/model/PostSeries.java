package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/** 편 수(postCount)는 저장하지 않는다 — {@code post_series_posts} 행 수에서 파생한다. */
@Entity
@Table(name = "post_series")
@Getter
public class PostSeries extends BaseEntity {

  @Column(name = "external_id", nullable = false, unique = true)
  private String externalId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private @Nullable Project project;

  @Column(name = "name", nullable = false)
  private String name;

  protected PostSeries() {}

  @Builder
  public PostSeries(String externalId, User creator, @Nullable Project project, String name) {
    this.externalId = externalId;
    this.creator = creator;
    this.project = project;
    this.name = name;
  }

  public void update(String name) {
    this.name = name;
  }

  public PostScope getScope() {
    return PostScope.fromProject(project);
  }

  public boolean isPersonal() {
    return getScope() == PostScope.PUBLIC;
  }

  public boolean isWorkspace() {
    return getScope() == PostScope.WORKSPACE;
  }
}
