package com.skkil.sync.experience.model;

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
@Table(name = "reflections")
@Getter
public class Reflection extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "experience_id", nullable = false)
  private Experience experience;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  protected Reflection() {}

  @Builder
  public Reflection(Experience experience, String content) {
    this.experience = experience;
    this.content = content;
  }
}
