package com.skkil.sync.project.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Teammates.TEAMMATES;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.project.dto.data.MyProjectDto;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectQueryRepository {

  private final DSLContext dsl;

  public ProjectQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<MyProjectDto> getMyProjects(Long userId) {
    var memberCounts =
        dsl.select(
                TEAMMATES.PROJECT_ID.as("projectId"),
                DSL.count().cast(Long.class).as("memberCount"))
            .from(TEAMMATES)
            .groupBy(TEAMMATES.PROJECT_ID)
            .asTable("member_counts");

    var unresolvedQuestionCounts =
        dsl.select(
                POSTS.PROJECT_ID.as("projectId"),
                DSL.count().cast(Long.class).as("unresolvedQuestionCount"))
            .from(POSTS)
            .where(
                POSTS
                    .POST_TYPE
                    .eq(PostType.QUESTION.name())
                    .and(POSTS.STATUS.eq(PostStatus.PUBLISHED.name()))
                    .and(POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name()))
                    .and(POSTS.RESOLVED.isFalse()))
            .groupBy(POSTS.PROJECT_ID)
            .asTable("unresolved_question_counts");

    Field<Long> memberCount = memberCounts.field("memberCount", Long.class);
    Field<Long> unresolvedQuestionCount =
        unresolvedQuestionCounts.field("unresolvedQuestionCount", Long.class);

    return dsl.select(
            PROJECTS.ID.as("id"),
            PROJECTS.HANDLE.as("handle"),
            PROJECTS.NAME.as("name"),
            PROJECTS.DESCRIPTION.as("description"),
            PROJECTS.WEBSITE_URL.as("website"),
            PROJECTS.IS_PUBLIC.as("isPublic"),
            PROJECTS.JOIN_POLICY.as("joinPolicy"),
            PROJECTS.FOLLOWER_COUNT.as("followerCount"),
            PROJECTS.ICON_MEDIA_ID.as("iconMediaId"),
            TEAMMATES.ROLE.as("role"),
            TEAMMATES.IS_OWNER.as("isOwner"),
            DSL.coalesce(memberCount, 0L).as("memberCount"),
            DSL.coalesce(unresolvedQuestionCount, 0L).as("unresolvedQuestionCount"))
        .from(TEAMMATES)
        .join(PROJECTS)
        .on(PROJECTS.ID.eq(TEAMMATES.PROJECT_ID))
        .leftJoin(memberCounts)
        .on(memberCounts.field("projectId", Long.class).eq(PROJECTS.ID))
        .leftJoin(unresolvedQuestionCounts)
        .on(unresolvedQuestionCounts.field("projectId", Long.class).eq(PROJECTS.ID))
        .where(TEAMMATES.USER_ID.eq(userId))
        .orderBy(PROJECTS.UPDATED_AT.desc(), PROJECTS.ID.desc())
        .fetchInto(MyProjectDto.class);
  }
}
