package com.skkil.sync.common.integration.slack;

import com.skkil.sync.common.integration.slack.dto.SlackMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
    ResponseEntity<Void> response =
        restClient
            .post()
            .uri(slackWebhookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message)
            .retrieve()
            .toBodilessEntity();
    log.debug(
        "Sent message to Slack: {}, response status: {}", message.text(), response.getStatusCode());
  }
}
