package com.skkil.sync.lab.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lab_members")
@Getter
public class LabMember extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lab_id", nullable = false)
  private Lab lab;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "review", columnDefinition = "TEXT")
  @Setter
  private String review;

  protected LabMember() {}

  @Builder
  public LabMember(Lab lab, User user, String review) {
    this.lab = lab;
    this.user = user;
    this.review = review;
  }
}
