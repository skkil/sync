package com.skkil.sync.badge.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
    name = "badge_collections",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "tag_id"})})
@Getter
public class BadgeCollection extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  @Column(name = "user_post_count", nullable = false)
  private Long postCount;

  @Column(name = "level", nullable = false)
  private Integer level;

  protected BadgeCollection() {}

  public BadgeCollection(User user, Tag tag, Long postCount, Integer level) {
    this.user = user;
    this.tag = tag;
    this.postCount = postCount;
    this.level = level;
  }

  public void updateProgress(Long postCount, Integer level) {
    this.postCount = postCount;
    this.level = level;
  }
}
