package com.skkil.sync.reflection.listener;

import com.skkil.sync.reflection.dto.data.ReflectionSummaryDto;
import com.skkil.sync.reflection.event.ReflectionCreatedEvent;
import com.skkil.sync.reflection.model.ReflectionSummary;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.reflection.repository.ReflectionSummaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class ReflectionCreatedEventListener {

  @Value("classpath:/prompts/reflection/summary.st")
  private Resource resource;

  private final ReflectionRepository reflectionRepository;
  private final ReflectionSummaryRepository reflectionSummaryRepository;
  private final ChatModel chatModel;

  public ReflectionCreatedEventListener(
      ReflectionRepository reflectionRepository,
      ReflectionSummaryRepository reflectionSummaryRepository,
      ChatModel chatModel) {
    this.reflectionRepository = reflectionRepository;
    this.reflectionSummaryRepository = reflectionSummaryRepository;
    this.chatModel = chatModel;
  }

  @Async
  @TransactionalEventListener
  public void createReflectionSummary(ReflectionCreatedEvent event) {
    log.debug("Handling ReflectionCreatedEvent {}", event.getReflectionId());

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);
    Message systemMessage = systemPromptTemplate.createMessage();
    Message userMessage = UserMessage.builder().text(event.getContent()).build();

    Prompt prompt = Prompt.builder().messages(systemMessage, userMessage).build();

    log.debug("Summarizing reflection {}", event.getReflectionId());
    ReflectionSummaryDto response =
        ChatClient.create(chatModel).prompt(prompt).call().entity(ReflectionSummaryDto.class);
    log.debug("Summarized reflection {}", event.getReflectionId());

    ReflectionSummary summary =
        ReflectionSummary.builder()
            .reflection(reflectionRepository.getReferenceById(event.getReflectionId()))
            .summary(response.summary())
            .build();

    reflectionSummaryRepository.save(summary);
    log.debug("Saved summary for reflection {}", event.getReflectionId());
  }
}
