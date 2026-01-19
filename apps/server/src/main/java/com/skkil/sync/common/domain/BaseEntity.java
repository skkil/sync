package com.skkil.sync.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity implements Persistable<Long>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter
  protected Long id;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  protected Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  protected Instant updatedAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
