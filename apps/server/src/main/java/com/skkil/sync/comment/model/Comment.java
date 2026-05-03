package com.skkil.sync.comment.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Reflection reflection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected Comment() {}

  @Builder
  public Comment(User author, Reflection reflection, Comment parent, String content) {
    this.author = author;
    this.reflection = reflection;
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
}
