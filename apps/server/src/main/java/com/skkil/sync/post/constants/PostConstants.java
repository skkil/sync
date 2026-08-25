package com.skkil.sync.post.constants;

public final class PostConstants {

  public static final int MAX_TAGS_PER_POST = 5;
  public static final int MAX_REFERENCES_PER_POST = 50;
  public static final int MAX_PINNED_POSTS_PER_PROJECT = 10;
  public static final int MAX_TOP_POSTS_PER_PROJECT = 5;
  public static final int TOP_POSTS_WINDOW_DAYS = 7;
  public static final int MAX_UNANSWERED_QUESTIONS_PER_PROJECT = 5;
  public static final int MAX_POSTS_PER_SERIES = 50;
  public static final int MAX_CONTENT_TEXT_LENGTH = 50_000;
  public static final int MAX_CONTENT_JSON_LENGTH = 500_000;
  public static final int MAX_POST_TEMPLATES_PER_PROJECT = 20;
  // 템플릿 목록 응답은 본문을 통째로 담으므로 게시글(500K)보다 좁게 잡는다.
  public static final int MAX_TEMPLATE_CONTENT_LENGTH = 100_000;
}
