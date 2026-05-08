package com.skkil.sync.comment.model;

import com.skkil.sync.comment.enums.CommentTargetType;
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
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "comments")
@Getter
public class Comment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 50)
  private CommentTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "like_count", nullable = false)
  private long likeCount;

  protected Comment() {}

  @Builder
  public Comment(
      User author, CommentTargetType targetType, Long targetId, Comment parent, String content) {
    this.author = author;
    this.targetType = targetType;
    this.targetId = targetId;
    this.parent = parent;
    this.content = content;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  public boolean isReply() {
    return parent != null;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void delete() {
    this.deletedAt = Instant.now();
  }

  public void incrementLikeCount() {
    likeCount++;
  }

  public void decrementLikeCount() {
    if (likeCount > 0) {
      likeCount--;
    }
  }
}
