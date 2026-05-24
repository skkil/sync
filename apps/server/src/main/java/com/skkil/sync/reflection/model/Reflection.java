package com.skkil.sync.reflection.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.experience.model.ProjectExperience;
import com.skkil.sync.reflection.constants.ReflectionConstants;
import com.skkil.sync.reflection.exception.ReflectionTagLimitExceededException;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "reflections")
@Getter
public class Reflection extends BaseEntity {

  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_experience_id", nullable = true)
  private ProjectExperience experience;

  @Column(name = "title")
  private String title;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @OneToMany(mappedBy = "reflection", fetch = FetchType.LAZY)
  private List<ReflectionTag> tags = new ArrayList<>();

  protected Reflection() {}

  @Builder
  public Reflection(String slug, User author, String title, String content) {
    this.slug = slug;
    this.author = author;
    this.title = title;
    this.content = content;
  }

  public void updateExperience(ProjectExperience experience) {
    this.experience = experience;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void addTag(ReflectionTag reflectionTag) {
    if (tags.size() >= ReflectionConstants.MAX_TAGS_PER_REFLECTION) {
      throw new ReflectionTagLimitExceededException();
    }

    this.tags.add(reflectionTag);
  }

  public void removeTag(Tag tag) {
    this.tags.removeIf(reflectionTag -> reflectionTag.getTag().equals(tag));
  }
}
