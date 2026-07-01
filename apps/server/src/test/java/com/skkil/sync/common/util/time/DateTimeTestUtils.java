package com.skkil.sync.common.util.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateTimeTestUtils {

  public static LocalDateTime defaultTestLocalDateTime() {
    return LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
  }

  public static OffsetDateTime defaultTestOffsetDateTime() {
    return OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
  }
}
