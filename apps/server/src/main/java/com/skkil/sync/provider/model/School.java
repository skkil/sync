package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.constant.SchoolType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "schools")
@Getter
public class School extends Provider {

  @Enumerated(EnumType.STRING)
  @Column(name = "school_type", length = 100, nullable = false)
  @Setter
  private SchoolType schoolType;

  protected School() {}

  public School(Long id) {
    super(ProviderType.SCHOOL, null, null, null, null);
    this.id = id;
  }

  @Builder
  public School(
      String name,
      String description,
      String contactInfo,
      String oneLineReview,
      SchoolType schoolType) {
    super(ProviderType.SCHOOL, name, description, contactInfo, oneLineReview);
    this.schoolType = schoolType;
  }
}
