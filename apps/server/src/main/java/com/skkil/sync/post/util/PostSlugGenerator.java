package com.skkil.sync.post.util;

import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.user.model.User;

public final class PostSlugGenerator {

  private PostSlugGenerator() {}

  public static String generate(User author, String title) {
    if (isBlank(title)) {
      return String.format("%s-%d", untitledSlugPrefix(author), System.currentTimeMillis());
    }

    return Slugify.slugify(title);
  }

  private static String untitledSlugPrefix(User author) {
    if (!isBlank(author.getHandle())) {
      return author.getHandle();
    }

    return author.getId() == null ? "post" : String.format("user-%d", author.getId());
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
