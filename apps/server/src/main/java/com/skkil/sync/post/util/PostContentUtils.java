package com.skkil.sync.post.util;

import com.skkil.sync.post.constants.PostPreviewProperties;

public final class PostContentUtils {

  private PostContentUtils() {}

  public static String getPreview(String text) {
    if (text == null) {
      return "";
    }

    if (text.length() <= PostPreviewProperties.PREVIEW_MAX_LENGTH) {
      return text;
    }

    return text.substring(0, PostPreviewProperties.PREVIEW_MAX_LENGTH) + "...";
  }

  public static int getWordCount(String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }

    return text.trim().split("\\s+").length;
  }
}
