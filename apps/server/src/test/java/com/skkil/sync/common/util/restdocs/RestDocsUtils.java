package com.skkil.sync.common.util.restdocs;

import org.springframework.restdocs.snippet.Attributes.Attribute;

public final class RestDocsUtils {

  private RestDocsUtils() {}

  public static final String ENUM_TYPE = "ENUM";

  public static Attribute[] getEnumAttributes(Class<? extends Enum<?>> clazz) {
    return new Attribute[] {new Attribute("enumValues", clazz.getEnumConstants())};
  }
}
