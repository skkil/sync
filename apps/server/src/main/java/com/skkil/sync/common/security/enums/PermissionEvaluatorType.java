package com.skkil.sync.common.security.enums;

public enum PermissionEvaluatorType {
  PROVIDER("PROVIDER"),
  EXPERIENCE("EXPERIENCE"),
  CONVERSATION("CONVERSATION");

  private final String type;

  PermissionEvaluatorType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static PermissionEvaluatorType fromString(String type) {
    for (PermissionEvaluatorType evaluatorType : PermissionEvaluatorType.values()) {
      if (evaluatorType.type.equalsIgnoreCase(type)) {
        return evaluatorType;
      }
    }

    throw new IllegalArgumentException("Unknown PermissionEvaluatorType: " + type);
  }
}
