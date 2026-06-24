package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.exception.PostTagLimitExceededException;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "posts")
@Getter
public class Post extends BaseEntity {

  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = true)
  private Project project;

  @Column(name = "title")
  private String title;

  @Column(name = "post_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private PostType type;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "like_count", nullable = false)
  private int likeCount = 0;

  @OneToMany(
      mappedBy = "post",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.ALL},
      orphanRemoval = true)
  private List<PostTag> tags = new ArrayList<>();

  protected Post() {}

  @Builder
  public Post(
      String slug, User author, Project project, String title, String content, PostType type) {
    this.slug = slug;
    this.author = author;
    this.project = project;
    this.title = title;
    this.content = content;
    this.type = type;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void addTag(PostTag postTag) {
    if (tags.size() >= PostConstants.MAX_TAGS_PER_POST) {
      throw new PostTagLimitExceededException();
    }

    this.tags.add(postTag);
  }

  public void removeTag(Tag tag) {
    this.tags.removeIf(postTag -> postTag.getTag().getName().equals(tag.getName()));
  }

  public void linkProject(Project project) {
    this.project = project;
  }

  public void unlinkProject() {
    this.project = null;
  }
}
