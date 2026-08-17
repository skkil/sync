package com.skkil.sync.notification.event;

import com.skkil.sync.notification.constant.NotificationEntityType;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.model.NotificationPayload;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEvent;

/**
 * {@code toString}은 비동기 처리 실패 로그에 그대로 실린다 — 기본 {@code EventObject} 폴백은 수신자 ID만 남겨 유실된 알림을 재구성할 수
 * 없으므로, 타입·행위자·대상까지 로그에서 복원 가능하도록 필드를 노출한다. 단 payload는 제외한다: 초대 payload의 토큰처럼 로그에 남기면 안 되는 값이 들어
 * 있고, 재구성에는 타입·수신자·entity 식별자만으로 충분하다.
 */
@Getter
@ToString
public class NotificationEvent extends ApplicationEvent {

  private final Long recipientId;
  private final NotificationType notificationType;
  private final @Nullable Long actorId;
  private final @Nullable NotificationEntityType entityType;
  private final @Nullable Long entityId;

  @ToString.Exclude private final NotificationPayload payload;

  public NotificationEvent(
      Long recipientId,
      NotificationType notificationType,
      @Nullable Long actorId,
      @Nullable NotificationEntityType entityType,
      @Nullable Long entityId,
      NotificationPayload payload) {
    super(recipientId);

    if ((entityType == null) != (entityId == null)) {
      throw new IllegalArgumentException("entityType and entityId must be set together");
    }

    this.recipientId = recipientId;
    this.notificationType = notificationType;
    this.actorId = actorId;
    this.entityType = entityType;
    this.entityId = entityId;
    this.payload = Objects.requireNonNull(payload, "payload must not be null");
  }
}
