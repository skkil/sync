package com.skkil.sync.project.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.media.model.Media;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @Column(name = "website_url")
  private String website;

  @ManyToOne
  @JoinColumn(name = "icon_media_id")
  private Media icon;

  @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<Teammate> teammates = new ArrayList<>();

  @Column(name = "is_public", nullable = false)
  private boolean isPublic = true;

  @Column(name = "follower_count", nullable = false)
  private long followerCount = 0;

  protected Project() {}

  @Builder
  public Project(String handle, String name, String description, boolean isPublic) {
    this.handle = handle == null ? Slugify.slugify(name) : handle.trim();
    this.name = name;
    this.isPublic = isPublic;

    if (description != null) {
      this.description = description;
    }
  }

  public void addTeammate(Teammate teammate) {
    teammates.add(teammate);
    teammate.setProject(this);
  }

  public void update(String description, String website) {
    if (description != null) {
      this.description = description;
    }

    if (website != null) {
      this.website = website;
    }
  }

  public void updateHandle(String handle) {
    this.handle = handle.trim();
  }

  public void removeIcon() {
    if (this.icon != null) {
      this.icon.markAsDeleted();
      this.icon = null;
    }
  }

  public void setIcon(Media icon) {
    if (this.icon != null) {
      this.icon.markAsDeleted();
    }

    this.icon = icon;
    icon.markAsUploaded();
  }
}
