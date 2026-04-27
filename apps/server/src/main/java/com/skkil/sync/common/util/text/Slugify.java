package com.skkil.sync.common.util.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class Slugify {

  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");

  public static String slugify(String input) {
    if (input == null) {
      return "";
    }

    String slug = WHITESPACE.matcher(input).replaceAll("-").trim();

    slug = Normalizer.normalize(slug, Normalizer.Form.NFD);

    slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("");

    slug =
        MULTIPLE_HYPHENS
            .matcher(slug)
            .replaceAll("-")
            .toLowerCase(Locale.ENGLISH)
            .replaceAll("^-|-$", "");

    slug = slug + "-" + System.currentTimeMillis();

    return slug;
  }
}
