package com.skkil.sync.user.constant;

import java.time.Duration;

public class EmailVerificationConstants {

  public static final int EMAIL_VERIFICATION_TOKEN_LENGTH = 6;

  public static final Duration EMAIL_VERIFICATION_TOKEN_TTL = Duration.ofMinutes(10);
}
