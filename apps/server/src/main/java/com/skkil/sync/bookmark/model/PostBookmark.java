package com.skkil.sync.bookmark.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.post.model.Post;
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
    name = "post_bookmarks",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_post_bookmarks_user_post",
          columnNames = {"user_id", "post_id"})
    })
@Getter
public class PostBookmark extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  protected PostBookmark() {}

  @Builder
  public PostBookmark(User user, Post post) {
    this.user = user;
    this.post = post;
  }
}
