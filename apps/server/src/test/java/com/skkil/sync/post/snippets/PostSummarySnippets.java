package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.summary.PostSummary;
import com.skkil.sync.post.dto.summary.TagSummary;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.project.snippets.ProjectSummarySnippets;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class PostSummarySnippets {

  public static PostSummary getPostSummary() {
    return getPostSummary(PostStatus.PUBLISHED);
  }

  public static PostSummary getPostSummary(PostStatus status) {
    return PostSummary.builder()
        .id(1L)
        .slug("test-slug")
        .title("Test Post Title")
        .type(PostType.SHORT)
        .status(status)
        .scope(PostScope.WORKSPACE)
        .author(UserSummarySnippets.getUserSummary())
        .project(ProjectSummarySnippets.getProjectSummary())
        .resolved(false)
        .isAuthor(false)
        .createdAt(DateTimeTestUtils.defaultTestOffsetDateTime())
        .updatedAt(DateTimeTestUtils.defaultTestOffsetDateTime())
        .likeCount(1L)
        .liked(true)
        .commentCount(1L)
        .bookmarked(true)
        .tags(
            List.of(
                TagSummary.builder()
                    .id(1L)
                    .name("java")
                    .description("자바 관련 태그")
                    .postCount(10L)
                    .followerCount(3L)
                    .isFollowing(false)
                    .build()))
        .preview("This is a preview of the post content.")
        .previewMedia(
            List.of(
                GetPostResponse.Media.builder()
                    .id(1L)
                    .url("https://example.com/image.png")
                    .build()))
        .mediaCount(1)
        .wordCount(120)
        .build();
  }

  public static List<FieldDescriptor> getPostSummaryFields(String prefix) {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath(prefix + "id").type(JsonFieldType.NUMBER).description("Post ID"));
    fields.add(fieldWithPath(prefix + "slug").type(JsonFieldType.STRING).description("Post Slug"));
    fields.add(
        fieldWithPath(prefix + "title")
            .type(JsonFieldType.STRING)
            .description("Post Title")
            .optional());
    fields.add(
        fieldWithPath(prefix + "type")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Type")
            .attributes(RestDocsUtils.getEnumAttributes(PostType.class)));
    fields.add(
        fieldWithPath(prefix + "status")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Status")
            .attributes(RestDocsUtils.getEnumAttributes(PostStatus.class)));
    fields.add(
        fieldWithPath(prefix + "scope")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("게시글 공개 범위")
            .attributes(RestDocsUtils.getEnumAttributes(PostScope.class)));
    fields.add(fieldWithPath(prefix + "author").type(JsonFieldType.OBJECT).description("작성자 정보"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields(prefix + "author."));
    fields.add(
        fieldWithPath(prefix + "project")
            .type(JsonFieldType.OBJECT)
            .description("소속 프로젝트 정보")
            .optional());
    ProjectSummarySnippets.getProjectSummaryFields(prefix + "project.").stream()
        .map(FieldDescriptor::optional)
        .forEach(fields::add);
    fields.add(
        fieldWithPath(prefix + "resolved")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the question post has been resolved"));
    fields.add(
        fieldWithPath(prefix + "isAuthor")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the requesting user is the author of this post"));
    fields.add(
        fieldWithPath(prefix + "createdAt")
            .type(JsonFieldType.STRING)
            .description("Creation Timestamp"));
    fields.add(
        fieldWithPath(prefix + "updatedAt")
            .type(JsonFieldType.STRING)
            .description("Last Updated Timestamp"));
    fields.add(
        fieldWithPath(prefix + "likeCount")
            .type(JsonFieldType.NUMBER)
            .description("Number of Likes"));
    fields.add(
        fieldWithPath(prefix + "liked")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the current user liked this post"));
    fields.add(
        fieldWithPath(prefix + "commentCount")
            .type(JsonFieldType.NUMBER)
            .description("Number of Comments"));
    fields.add(
        fieldWithPath(prefix + "bookmarked")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the current user bookmarked this post"));
    fields.add(
        fieldWithPath(prefix + "tags").type(JsonFieldType.ARRAY).description("게시물에 달린 태그 목록"));
    fields.add(fieldWithPath(prefix + "tags[].id").type(JsonFieldType.NUMBER).description("태그 ID"));
    fields.add(
        fieldWithPath(prefix + "tags[].name").type(JsonFieldType.STRING).description("태그 이름"));
    fields.add(
        fieldWithPath(prefix + "tags[].description")
            .type(JsonFieldType.STRING)
            .description("태그 설명"));
    fields.add(
        fieldWithPath(prefix + "tags[].postCount")
            .type(JsonFieldType.NUMBER)
            .description("태그가 사용된 게시물 수"));
    fields.add(
        fieldWithPath(prefix + "tags[].followerCount")
            .type(JsonFieldType.NUMBER)
            .description("태그를 팔로우하는 사용자 수"));
    fields.add(
        fieldWithPath(prefix + "tags[].projectHandle")
            .type(JsonFieldType.STRING)
            .description("프로젝트 태그인 경우 해당 프로젝트의 핸들 (전역 태그인 경우 없음)")
            .optional());
    fields.add(
        fieldWithPath(prefix + "tags[].isFollowing")
            .type(JsonFieldType.BOOLEAN)
            .description("요청자가 해당 태그를 팔로우하고 있는지 여부 (프로젝트 태그는 항상 false)"));
    fields.add(
        fieldWithPath(prefix + "preview")
            .type(JsonFieldType.STRING)
            .description("게시물 내용의 일반 텍스트 미리보기"));
    fields.add(
        fieldWithPath(prefix + "previewMedia")
            .type(JsonFieldType.ARRAY)
            .description("미리보기용 첨부 미디어 목록 (최대 2개)"));
    fields.add(
        fieldWithPath(prefix + "previewMedia[].id")
            .type(JsonFieldType.NUMBER)
            .description("미디어 ID"));
    fields.add(
        fieldWithPath(prefix + "previewMedia[].url")
            .type(JsonFieldType.STRING)
            .description("미디어 URL"));
    fields.add(
        fieldWithPath(prefix + "mediaCount")
            .type(JsonFieldType.NUMBER)
            .description("게시물에 첨부된 전체 미디어 수"));
    fields.add(
        fieldWithPath(prefix + "wordCount").type(JsonFieldType.NUMBER).description("게시물 본문의 단어 수"));
    return fields;
  }
}
