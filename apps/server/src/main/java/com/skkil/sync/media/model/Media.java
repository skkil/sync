package com.skkil.sync.media.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "media_files")
@Getter
public class Media extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploader_id", nullable = false)
  private User uploader;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private MediaStatus status;

  @Column(name = "media_type", nullable = false)
  private String mediaType;

  @Column(name = "bucket", nullable = false)
  private String bucket;

  @Column(name = "key", nullable = false)
  private String key;

  @Column(name = "filename", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long fileSize;

  protected Media() {}

  @Builder
  public Media(
      User uploader, String mediaType, String bucket, String key, String fileName, Long fileSize) {
    this.uploader = uploader;
    this.status = MediaStatus.PENDING;
    this.mediaType = mediaType;
    this.bucket = bucket;
    this.key = key;
    this.fileName = fileName;
    this.fileSize = fileSize;
  }

  public void markAsUploaded() {
    this.status = MediaStatus.UPLOADED;
  }

  public void markAsDeleted() {
    this.status = MediaStatus.DELETED;
  }
}
