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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_experience_id", nullable = true)
  private ProjectExperience experience;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  protected Reflection() {}

  @Builder
  public Reflection(User author, String content) {
    this.author = author;
    this.content = content;
  }

  public void updateExperience(ProjectExperience experience) {
    this.experience = experience;
  }

  public void updateContent(String content) {
    this.content = content;
  }
}
