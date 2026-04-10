package com.skkil.sync.reflection.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "reflection_summaries")
@Getter
public class ReflectionSummary {

  @Id private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private Reflection reflection;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  protected ReflectionSummary() {}

  @Builder
  public ReflectionSummary(Reflection reflection, String summary) {
    this.reflection = reflection;
    this.summary = summary;
  }
}
