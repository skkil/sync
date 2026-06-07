package com.skkil.sync.notification.repository;

import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.model.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query(
      value =
          """
          SELECT n FROM Notification n
          LEFT JOIN FETCH n.actor
          WHERE n.user.id = :userId
          AND (:status IS NULL OR n.status = :status)
          AND (:cursor IS NULL OR n.id < :cursor)
          ORDER BY n.id DESC
          """,
      countQuery =
          """
          SELECT COUNT(n) FROM Notification n
          WHERE n.user.id = :userId
          AND (:status IS NULL OR n.status = :status)
          AND (:cursor IS NULL OR n.id < :cursor)
          """)
  public Page<Notification> findByUser(
      Long userId, NotificationStatus status, Pageable pageable, Long cursor);

  public Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

  @Modifying
  @Query(
      """
      UPDATE Notification n
      SET n.status = :status
      WHERE n.user.id = :userId
      AND n.status = :currentStatus
      """)
  public int updateStatusByUser(
      Long userId, NotificationStatus currentStatus, NotificationStatus status);
}
