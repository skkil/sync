package com.skkil.sync.bookmark.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.reflection.model.Reflection;
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
    name = "reflection_bookmarks",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_reflection_bookmarks_user_reflection",
          columnNames = {"user_id", "reflection_id"})
    })
@Getter
public class ReflectionBookmark extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reflection_id", nullable = false)
  private Reflection reflection;

  protected ReflectionBookmark() {}

  @Builder
  public ReflectionBookmark(User user, Reflection reflection) {
    this.user = user;
    this.reflection = reflection;
  }
}
