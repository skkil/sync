package com.skkil.sync.provider.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "team_building_posts")
@Getter
public class TeamBuildingPost extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "like_count", nullable = false)
  private long likeCount;

  @Column(name = "comment_count", nullable = false)
  private long commentCount;

  protected TeamBuildingPost() {}

  @Builder
  public TeamBuildingPost(Project project, String title, String content) {
    this.project = project;
    this.title = title;
    this.content = content;
  }

  public void incrementLikeCount() {
    likeCount++;
  }

  public void decrementLikeCount() {
    if (likeCount > 0) {
      likeCount--;
    }
  }

  public void incrementCommentCount() {
    commentCount++;
  }

  public void decrementCommentCount() {
    if (commentCount > 0) {
      commentCount--;
    }
  }
}
