package com.skkil.sync.notification.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "notifications")
@Getter
public class Notification extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private NotificationStatus status = NotificationStatus.UNREAD;

  protected Notification() {}

  @Builder
  public Notification(User user, NotificationType type) {
    this.user = user;
    this.type = type;
  }
}
