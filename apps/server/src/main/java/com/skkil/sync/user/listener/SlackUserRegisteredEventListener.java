package com.skkil.sync.user.listener;

import com.skkil.sync.common.integration.slack.SlackService;
import com.skkil.sync.common.integration.slack.dto.SlackMessage;
import com.skkil.sync.user.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class SlackUserRegisteredEventListener {

  private final SlackService slackService;

  public SlackUserRegisteredEventListener(SlackService slackService) {
    this.slackService = slackService;
  }

  @Async
  @TransactionalEventListener
  public void handleUserRegisteredEvent(UserRegisteredEvent event) {
    log.debug("User registered event received for user ID: {}", event.getUserId());
    slackService.sendMessage(new SlackMessage("새로운 사용자가 가입했습니다!"));
  }
}
