package com.skkil.sync.provider.contest.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "contest_occurrences")
@Getter
public class ContestOccurrence extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contest_id", nullable = false)
  private Contest contest;

  @Column(name = "occurrence_name", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  protected ContestOccurrence() {}

  @Builder
  public ContestOccurrence(Contest contest, String title, String description) {
    this.contest = contest;
    this.title = title;
    this.description = description;
  }
}
