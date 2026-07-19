package com.skkil.sync.common.util.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class Slugify {

  private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{Mn}+");
  private static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[^\\p{L}\\p{N}-]+");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");
  private static final Pattern LEADING_OR_TRAILING_HYPHENS = Pattern.compile("^-|-$");

  public static String slugify(String input) {
    if (input == null) {
      return "";
    }

    String slug = input.toLowerCase(Locale.ENGLISH);

    slug = WHITESPACE.matcher(slug).replaceAll("-").trim();

    // NFD splits accented Latin letters into a base letter plus a combining mark (e.g. é
    // becomes e + ´), which lets us drop just the mark. Hangul syllables also decompose
    // under NFD into individual jamo, so re-normalize to NFC afterward to recompose them
    // instead of leaving a slug full of separate jamo characters.
    slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
    slug = COMBINING_MARKS.matcher(slug).replaceAll("");
    slug = Normalizer.normalize(slug, Normalizer.Form.NFC);

    slug = DISALLOWED_CHARACTERS.matcher(slug).replaceAll("");

    slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
    slug = LEADING_OR_TRAILING_HYPHENS.matcher(slug).replaceAll("");

    if (slug.isEmpty()) {
      return String.valueOf(System.currentTimeMillis());
    }

    return slug + "-" + System.currentTimeMillis();
  }
}
