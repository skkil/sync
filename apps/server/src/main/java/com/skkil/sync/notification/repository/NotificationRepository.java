package com.skkil.sync.notification.repository;

import com.skkil.sync.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query(
      """
      SELECT n FROM Notification n
      WHERE n.user.id = :userId
      AND (:cursor IS NULL OR n.id < :cursor)
      ORDER BY n.id DESC
      """)
  public Page<Notification> findByUser(Long userId, Pageable pageable, Long cursor);
}
