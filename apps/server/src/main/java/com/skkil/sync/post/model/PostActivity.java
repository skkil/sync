package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Table(
    name = "post_activities",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"})})
@Getter
public class PostActivity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "count", nullable = false)
  private Long count = 0L;

  protected PostActivity() {}
}
