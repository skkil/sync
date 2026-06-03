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

  @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<Teammate> teammates = new ArrayList<>();

  protected Project() {}

  @Builder
  public Project(String handle, String name) {
    this.handle = handle == null ? Slugify.slugify(handle) : handle.trim();
    this.name = name;
  }

  public void addTeammate(Teammate teammate) {
    teammates.add(teammate);
    teammate.setProject(this);
  }
}
