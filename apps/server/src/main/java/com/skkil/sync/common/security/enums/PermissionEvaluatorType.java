package com.skkil.sync.common.security.enums;

public enum PermissionEvaluatorType {
  PROFILE("PROFILE"),
  PROVIDER("PROVIDER"),
  EXPERIENCE("EXPERIENCE"),
  REFLECTION("REFLECTION"),
  COMMENT("COMMENT"),
  JOB_APPLICATION("JOB_APPLICATION");

  private final String value;

  PermissionEvaluatorType(String value) {
    this.value = value;
  }

  public static PermissionEvaluatorType of(String value) {
    for (PermissionEvaluatorType type : PermissionEvaluatorType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }

    throw new IllegalArgumentException("Unknown PermissionEvaluatorType: " + value);
  }
}
