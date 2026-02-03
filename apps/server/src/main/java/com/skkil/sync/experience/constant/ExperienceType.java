package com.skkil.sync.experience.constant;

import com.skkil.sync.provider.constant.ProviderType;

public enum ExperienceType {
  EDUCATION(ProviderType.SCHOOL),
  EMPLOYMENT(ProviderType.COMPANY),
  AWARD(ProviderType.CONTEST),
  CERTIFICATION(ProviderType.CERTIFICATE),
  PROJECT_EXPERIENCE(ProviderType.PROJECT);

  private final ProviderType providerType;

  ExperienceType(ProviderType providerType) {
    this.providerType = providerType;
  }

  public ProviderType getProviderType() {
    return providerType;
  }

  public static class Constants {
    public static final String EDUCATION = "EDUCATION";
    public static final String EMPLOYMENT = "EMPLOYMENT";
    public static final String AWARD = "AWARD";
    public static final String CERTIFICATION = "CERTIFICATION";
    public static final String PROJECT_EXPERIENCE = "PROJECT_EXPERIENCE";
  }
}
