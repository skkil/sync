package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;

@Entity
@Table(name = "post_reports")
@Getter
public class PostReport extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private User reporter;

  @Column(name = "reason", nullable = false)
  @Enumerated(EnumType.STRING)
  private PostReportReason reason;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private PostReportStatus status = PostReportStatus.PENDING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewed_by")
  private User reviewedBy;

  @Column(name = "reviewed_at")
  private Instant reviewedAt;

  @Column(name = "resolution_note", columnDefinition = "TEXT")
  private String resolutionNote;

  protected PostReport() {}

  public PostReport(Post post, User reporter, PostReportReason reason, String description) {
    this.post = post;
    this.reporter = reporter;
    this.reason = reason;
    this.description = description;
  }

  public boolean isReviewed() {
    return status != PostReportStatus.PENDING;
  }

  public void dismiss(User reviewer, String note) {
    review(PostReportStatus.DISMISSED, reviewer, note);
  }

  public void resolve(User reviewer, String note) {
    review(PostReportStatus.RESOLVED, reviewer, note);
  }

  private void review(PostReportStatus status, User reviewer, String note) {
    this.status = status;
    this.reviewedBy = reviewer;
    this.reviewedAt = Instant.now();
    this.resolutionNote = note;
  }
}
