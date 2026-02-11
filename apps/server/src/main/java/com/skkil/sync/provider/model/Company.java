package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
public class Company extends Provider {

  @Column(name = "company_size")
  @Setter
  private Integer companySize;

  protected Company() {}

  @Builder
  public Company(
      String name,
      String description,
      String contactInfo,
      String oneLineReview,
      Integer companySize) {
    super(ProviderType.COMPANY, name, description, contactInfo, oneLineReview);
    this.companySize = companySize;
  }
}
