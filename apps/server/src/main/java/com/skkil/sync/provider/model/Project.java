package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "projects")
@Getter
public class Project extends Provider {

  protected Project() {}

  @Builder
  public Project(String name, String description, String contactInfo, String oneLineReview) {
    super(ProviderType.PROJECT, name, description, contactInfo, oneLineReview);
  }
}
