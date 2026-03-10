package com.skkil.sync.provider.model.company;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Provider;
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

  @Column(name = "industry")
  private String industry;

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
      String industry,
      Integer companySize) {
    super(ProviderType.COMPANY, name, description, contactInfo, oneLineReview);
    this.industry = industry;
    this.companySize = companySize;
  }

  public void updateFields(String name, String description, String industry) {
    if (name != null) {
      setName(name);
    }

    if (description != null) {
      setDescription(description);
    }

    if (industry != null) {
      this.industry = industry;
    }
  }
}
