package com.skkil.sync.provider.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.provider.constant.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "providers")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Provider extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "provider_type", length = 100, nullable = false)
  private ProviderType type;

  @Column(name = "name", nullable = false, length = 255)
  @Setter
  protected String name;

  @Column(name = "description", columnDefinition = "TEXT")
  @Setter
  protected String description;

  @Column(name = "contact_info", length = 255)
  @Setter
  protected String contactInfo;

  @Column(name = "one_line_review", length = 500)
  @Setter
  private String oneLineReview;

  protected Boolean isVerified = false;

  protected Provider() {}

  protected Provider(
      ProviderType type, String name, String description, String contactInfo, String oneLineReview) {
    this.type = type;
    this.name = name;
    this.description = description;
    this.contactInfo = contactInfo;
    this.oneLineReview = oneLineReview;
  }
}
