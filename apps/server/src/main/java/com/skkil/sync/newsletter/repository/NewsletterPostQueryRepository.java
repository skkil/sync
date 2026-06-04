package com.skkil.sync.newsletter.repository;

import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.newsletter.dto.NewsletterPostCandidate;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class NewsletterPostQueryRepository {

  private final DSLContext dsl;

  public NewsletterPostQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<NewsletterPostCandidate> findLatestReflections(int limit) {
    return dsl.select(
            REFLECTIONS.ID.as("id"),
            REFLECTIONS.SLUG.as("slug"),
            REFLECTIONS.TITLE.as("title"),
            REFLECTIONS.CONTENT.as("content"),
            USERS.FULL_NAME.as("authorName"),
            REFLECTIONS.CREATED_AT.as("createdAt"))
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .orderBy(REFLECTIONS.CREATED_AT.desc(), REFLECTIONS.ID.desc())
        .limit(limit)
        .fetchInto(NewsletterPostCandidate.class);
  }
}
