package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.media.model.Media;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "post_media_files")
@Getter
public class PostMediaFile extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media_id", nullable = false)
  private Media media;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  protected PostMediaFile() {}

  public PostMediaFile(Post post, Media media, int sortOrder) {
    this.post = post;
    this.media = media;
    this.sortOrder = sortOrder;
  }
}
