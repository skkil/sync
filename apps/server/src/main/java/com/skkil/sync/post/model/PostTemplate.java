package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * 프로젝트 관리자가 정의하는 글 템플릿. 팀원이 프로젝트에서 글을 쓸 때 골라서 제목 접두어({@code title})와 본문 스캐폴드({@code content})를 미리
 * 채우는 용도다.
 */
@Entity
@Table(name = "post_templates")
@Getter
public class PostTemplate extends BaseEntity {

  @Column(name = "external_id", nullable = false, unique = true)
  private String externalId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(name = "name", nullable = false)
  private String name;

  /** 템플릿 적용 시 글 제목 칸에 채울 접두어. 없으면 제목은 건드리지 않는다. */
  @Column(name = "title")
  private @Nullable String title;

  /** 게시글 본문과 같은 Tiptap JSON 문자열. 미디어 노드는 담지 않는다. */
  @Column(name = "content", nullable = false)
  private String content;

  protected PostTemplate() {}

  @Builder
  public PostTemplate(
      String externalId,
      User creator,
      Project project,
      String name,
      @Nullable String title,
      String content) {
    this.externalId = externalId;
    this.creator = creator;
    this.project = project;
    this.name = name;
    this.title = title;
    this.content = content;
  }

  public void update(String name, @Nullable String title, String content) {
    this.name = name;
    this.title = title;
    this.content = content;
  }
}
