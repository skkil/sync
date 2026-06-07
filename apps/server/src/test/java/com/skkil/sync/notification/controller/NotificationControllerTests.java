package com.skkil.sync.notification.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.service.NotificationService;
import com.skkil.sync.notification.snippets.GetNotificationsResponseSnippets;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class NotificationControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private NotificationService notificationService;

  @Test
  @DisplayName("[getNotifications] API 문서화 테스트")
  @WithAuthenticatedUser
  void getNotifications() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetNotificationsResponse response =
        GetNotificationsResponseSnippets.getGetNotificationsResponse();

    when(notificationService.getNotifications(
            eq(user.userId()), eq(10), eq(20L), eq(NotificationStatus.UNREAD)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/notifications")
                .param("size", "10")
                .param("cursor", "20")
                .param("status", "UNREAD"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetNotifications",
                ResourceSnippetParameters.builder()
                    .tag("notification")
                    .summary("Get Notifications")
                    .description("Get Notifications")
                    .responseSchema(schema(GetNotificationsResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                queryParameters(
                    parameterWithName("size").description("Page size").optional(),
                    parameterWithName("cursor").description("Cursor notification ID").optional(),
                    parameterWithName("status").description("Notification status").optional()),
                GetNotificationsResponseSnippets.getNotificationsResponseFields()));
  }

  @Test
  @DisplayName("[markNotificationAsRead] API 문서화 테스트")
  @WithAuthenticatedUser
  void markNotificationAsRead() throws Exception {
    mockMvc
        .perform(patch("/notifications/{notificationId}/read", 1L))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "MarkNotificationAsRead",
                ResourceSnippetParameters.builder()
                    .tag("notification")
                    .summary("Mark Notification As Read")
                    .description("Mark Notification As Read"),
                null,
                null,
                Function.identity(),
                pathParameters(
                    parameterWithName("notificationId").description("Notification ID"))));
  }

  @Test
  @DisplayName("[markAllNotificationsAsRead] API 문서화 테스트")
  @WithAuthenticatedUser
  void markAllNotificationsAsRead() throws Exception {
    mockMvc
        .perform(patch("/notifications/read-all"))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "MarkAllNotificationsAsRead",
                ResourceSnippetParameters.builder()
                    .tag("notification")
                    .summary("Mark All Notifications As Read")
                    .description("Mark All Notifications As Read"),
                null,
                null,
                Function.identity()));
  }
}
