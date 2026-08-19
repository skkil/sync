package com.skkil.sync.notification.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = WelcomePayload.class, name = "WELCOME"),
  @JsonSubTypes.Type(value = NewCommentPayload.class, name = "NEW_COMMENT"),
  @JsonSubTypes.Type(value = NewLikePayload.class, name = "NEW_LIKE"),
  @JsonSubTypes.Type(value = NewFollowerPayload.class, name = "NEW_FOLLOWER"),
  @JsonSubTypes.Type(value = ProjectInvitationPayload.class, name = "PROJECT_INVITATION"),
  @JsonSubTypes.Type(
      value = ProjectInvitationAcceptedPayload.class,
      name = "PROJECT_INVITATION_ACCEPTED"),
  @JsonSubTypes.Type(
      value = ProjectInvitationDeclinedPayload.class,
      name = "PROJECT_INVITATION_DECLINED"),
  @JsonSubTypes.Type(value = ProjectJoinRequestPayload.class, name = "PROJECT_JOIN_REQUEST"),
  @JsonSubTypes.Type(
      value = ProjectJoinRequestApprovedPayload.class,
      name = "PROJECT_JOIN_REQUEST_APPROVED"),
  @JsonSubTypes.Type(
      value = ProjectJoinRequestDeclinedPayload.class,
      name = "PROJECT_JOIN_REQUEST_DECLINED"),
  @JsonSubTypes.Type(value = NewMessagePayload.class, name = "NEW_MESSAGE"),
})
public sealed interface NotificationPayload
    permits WelcomePayload,
        NewCommentPayload,
        NewLikePayload,
        NewFollowerPayload,
        ProjectInvitationPayload,
        ProjectInvitationAcceptedPayload,
        ProjectInvitationDeclinedPayload,
        ProjectJoinRequestPayload,
        ProjectJoinRequestApprovedPayload,
        ProjectJoinRequestDeclinedPayload,
        NewMessagePayload {}
