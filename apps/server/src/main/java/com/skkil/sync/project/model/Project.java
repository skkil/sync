package com.skkil.sync.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.common.util.text.Slugify;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "projects")
@Getter
public class Project extends BaseEntity {

  @Column(name = "handle", nullable = false, unique = true)
  private String handle;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<Teammate> teammates = new ArrayList<>();

  @Column(name = "is_public", nullable = false)
  private boolean isPublic = true;

  protected Project() {}

  @Builder
  public Project(String handle, String name) {
    this.handle = handle == null ? Slugify.slugify(name) : handle.trim();
    this.name = name;
  }

  public void addTeammate(Teammate teammate) {
    teammates.add(teammate);
    teammate.setProject(this);
  }

  public void update(String name, String description) {
    if (name != null) {
      this.name = name;
    }

    if (description != null) {
      this.description = description;
    }
  }

  public void updateHandle(String handle) {
    this.handle = handle.trim();
  }
}
