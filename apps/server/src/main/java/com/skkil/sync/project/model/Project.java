package com.skkil.sync.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "projects")
@Getter
public class Project extends BaseEntity {

  @Column(name = "name", nullable = false)
  private String name;

  protected Project() {}

  @Builder
  public Project(String name) {
    this.name = name;
  }
}
