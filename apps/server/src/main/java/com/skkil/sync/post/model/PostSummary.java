package com.skkil.sync.post.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "post_summaries")
@Getter
public class PostSummary {

  @Id private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private Post post;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  protected PostSummary() {}

  @Builder
  public PostSummary(Post post, String summary) {
    this.post = post;
    this.summary = summary;
  }

  public void updateSummary(String summary) {
    this.summary = summary;
  }
}
