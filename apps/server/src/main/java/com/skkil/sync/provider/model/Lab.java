package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "labs")
@Getter
public class Lab extends Provider {

  @Column(name = "professor_id")
  @Setter
  private String professorId;

  @ManyToOne
  @JoinColumn(name = "school_id", nullable = false)
  @Setter
  private School school;

  @Column(name = "research_area", columnDefinition = "TEXT")
  @Setter
  private String researchArea;

  @Column(name = "detailed_research_field", columnDefinition = "TEXT")
  @Setter
  private String detailedResearchField;

  @Column(name = "one_line_review", length = 500)
  @Setter
  private String oneLineReview;

  protected Lab() {}

  @Builder
  public Lab(
      String name,
      String description,
      String contactInfo,
      String professorId,
      School school,
      String researchArea,
      String detailedResearchField,
      String oneLineReview) {
    super(ProviderType.LAB, name, description);
    this.contactInfo = contactInfo;
    this.professorId = professorId;
    this.school = school;
    this.researchArea = researchArea;
    this.detailedResearchField = detailedResearchField;
    this.oneLineReview = oneLineReview;
  }
}
