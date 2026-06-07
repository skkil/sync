package com.skkil.sync.notification.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetNotificationsResponseSnippets {

  public static GetNotificationsResponse getGetNotificationsResponse() {
    GetNotificationsResponse.Actor actor =
        new GetNotificationsResponse.Actor(1L, "follower", "Follower User");

    GetNotificationsResponse.Notification notification =
        new GetNotificationsResponse.Notification(
            1L,
            NotificationType.USER_FOLLOWED,
            NotificationStatus.UNREAD,
            Instant.parse("2026-01-01T00:00:00Z"),
            actor);

    return new GetNotificationsResponse(CursorPaginationResponseSnippets.of(List.of(notification)));
  }

  public static ResponseFieldsSnippet getNotificationsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("notifications");

    fields =
        fields.andWithPrefix(
            "notifications.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Notification ID"),
            fieldWithPath(".type").type(JsonFieldType.STRING).description("Notification type"),
            fieldWithPath(".status").type(JsonFieldType.STRING).description("Notification status"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation timestamp"),
            fieldWithPath(".actor")
                .type(JsonFieldType.OBJECT)
                .description("Notification actor")
                .optional());

    fields =
        fields.andWithPrefix(
            "notifications.nodes[].content.actor",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Actor user ID").optional(),
            fieldWithPath(".handle")
                .type(JsonFieldType.STRING)
                .description("Actor handle")
                .optional(),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Actor name").optional());

    return responseFields(fields.getFieldDescriptors());
  }
}
