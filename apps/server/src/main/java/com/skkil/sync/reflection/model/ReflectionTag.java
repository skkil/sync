package com.skkil.sync.reflection.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "reflection_tags")
@Getter
public class ReflectionTag extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reflection_id", nullable = false)
  private Reflection reflection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  protected ReflectionTag() {}

  @Builder
  public ReflectionTag(Reflection reflection, Tag tag) {
    this.reflection = reflection;
    this.tag = tag;
  }
}
