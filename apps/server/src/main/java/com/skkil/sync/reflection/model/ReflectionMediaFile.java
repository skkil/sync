package com.skkil.sync.reflection.model;

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
@Table(name = "reflection_media_files")
@Getter
public class ReflectionMediaFile extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reflection_id", nullable = false)
  private Reflection reflection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media_id", nullable = false)
  private Media media;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  protected ReflectionMediaFile() {}

  public ReflectionMediaFile(Reflection reflection, Media media, int sortOrder) {
    this.reflection = reflection;
    this.media = media;
    this.sortOrder = sortOrder;
  }
}
