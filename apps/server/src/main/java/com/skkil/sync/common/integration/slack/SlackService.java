package com.skkil.sync.common.integration.slack;

import com.skkil.sync.common.integration.slack.dto.SlackMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class SlackService {

  @Value("${app.slack.webhook-url}")
  private String slackWebhookUrl;

  private final RestClient restClient;

  public SlackService() {
    this.restClient = RestClient.builder().build();
  }

  public void sendMessage(SlackMessage message) {
    if (slackWebhookUrl == null || slackWebhookUrl.isEmpty()) {
      log.warn("Slack webhook URL is not configured. Skipping sending message: {}", message.text());
      return;
    }

    try {
      ResponseEntity<Void> response =
          restClient
              .post()
              .uri(slackWebhookUrl)
              .contentType(MediaType.APPLICATION_JSON)
              .body(message)
              .retrieve()
              .toBodilessEntity();

      log.debug(
          "Sent message to Slack: {}, response status: {}",
          message.text(),
          response.getStatusCode());

    } catch (RestClientException e) {
      log.warn("Failed to send message to Slack: {}, error: {}", message.text(), e.getMessage());
    }
  }
}
