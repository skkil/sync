package com.skkil.sync.lab.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.provider.model.School;
import com.skkil.sync.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "labs")
@Getter
public class Lab extends BaseEntity {

  @Column(name = "name", nullable = false, length = 255)
  @Setter
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  @Setter
  private String description;

  @Column(name = "one_line_review", length = 500)
  @Setter
  private String oneLineReview;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "professor_id", nullable = false)
  @Setter
  private User professor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id", nullable = false)
  @Setter
  private School school;

  @Column(name = "research_area", columnDefinition = "TEXT")
  @Setter
  private String researchArea;

  @Column(name = "detailed_research_field", columnDefinition = "TEXT")
  @Setter
  private String detailedResearchField;

  @Column(name = "contact_info", length = 255)
  @Setter
  private String contactInfo;

  @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<LabMember> members = new HashSet<>();

  protected Lab() {}

  @Builder
  public Lab(
      String name,
      String description,
      String oneLineReview,
      User professor,
      School school,
      String researchArea,
      String detailedResearchField,
      String contactInfo) {
    this.name = name;
    this.description = description;
    this.oneLineReview = oneLineReview;
    this.professor = professor;
    this.school = school;
    this.researchArea = researchArea;
    this.detailedResearchField = detailedResearchField;
    this.contactInfo = contactInfo;
  }

  public void addMember(User user) {
    LabMember labMember = LabMember.builder().lab(this).user(user).build();
    this.members.add(labMember);
  }

  public void addMember(User user, String review) {
    LabMember labMember = LabMember.builder().lab(this).user(user).review(review).build();
    this.members.add(labMember);
  }

  public void removeMember(User user) {
    this.members.removeIf(member -> member.getUser().getId().equals(user.getId()));
  }
}
