package com.skkil.sync.reflection.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "tags")
@Getter
public class Tag extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "post_count", nullable = false)
  private Long postCount;

  @Column(name = "verified", nullable = false)
  private boolean verified = false;

  protected Tag() {}

  @Builder
  public Tag(String name) {
    this.name = name;
    this.description = "";
    this.postCount = 0L;
  }

  public void verify() {
    this.verified = true;
  }

  public void unverify() {
    this.verified = false;
  }

  public void updateDescription(String description) {
    this.description = description;
  }
}
