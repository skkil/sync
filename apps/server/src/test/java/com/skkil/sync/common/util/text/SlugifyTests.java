package com.skkil.sync.common.util.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SlugifyTests {

  @Test
  void slugify_whenKoreanTitle_thenPreservesHangulInsteadOfFallingBackToTimestamp() {
    String slug = Slugify.slugify("안녕하세요 반갑습니다");

    assertThat(slug).startsWith("안녕하세요-반갑습니다-");
  }

  @Test
  void slugify_whenAccentedLatinTitle_thenFoldsDiacritics() {
    String slug = Slugify.slugify("Café Résumé");

    assertThat(slug).startsWith("cafe-resume-");
  }

  @Test
  void slugify_whenAsciiTitle_thenLowercasesAndHyphenates() {
    String slug = Slugify.slugify("Hello World");

    assertThat(slug).startsWith("hello-world-");
  }

  @Test
  void slugify_whenOnlyPunctuation_thenFallsBackToTimestamp() {
    String slug = Slugify.slugify("!!!???");

    assertThat(slug).matches("\\d+");
  }

  @Test
  void slugify_whenNull_thenReturnsEmptyString() {
    assertThat(Slugify.slugify(null)).isEmpty();
  }
}
