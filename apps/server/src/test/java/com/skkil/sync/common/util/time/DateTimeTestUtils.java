package com.skkil.sync.common.util.time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeTestUtils {

  public static LocalDateTime defaultTestLocalDateTime() {
    return LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
  }
}
