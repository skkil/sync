package com.skkil.sync.reflection.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.experience.model.ProjectExperience;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "reflections")
@Getter
public class Reflection extends BaseEntity {

  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_experience_id", nullable = true)
  private ProjectExperience experience;

  @Column(name = "title")
  private String title;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "like_count", nullable = false)
  private long likeCount;

  @Column(name = "comment_count", nullable = false)
  private long commentCount;

  protected Reflection() {}

  @Builder
  public Reflection(String slug, User author, String title, String content) {
    this.slug = slug;
    this.author = author;
    this.title = title;
    this.content = content;
  }

  public void updateExperience(ProjectExperience experience) {
    this.experience = experience;
  }

  public void updateContent(String content) {
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
