package com.skkil.sync.reflection.listener;

import com.skkil.sync.reflection.dto.data.ReflectionSummaryDto;
import com.skkil.sync.reflection.event.ReflectionCreatedEvent;
import com.skkil.sync.reflection.model.ReflectionSummary;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.reflection.repository.ReflectionSummaryRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class ReflectionCreatedEventListener {

  private static final int MINIMUM_SUMMARIZABLE_CONTENT_LENGTH = 200;

  @Value("classpath:/prompts/reflection/summary.st")
  private Resource resource;

  private final ReflectionRepository reflectionRepository;
  private final ReflectionSummaryRepository reflectionSummaryRepository;
  private final VectorStore vectorStore;
  private final ChatModel chatModel;

  public ReflectionCreatedEventListener(
      ReflectionRepository reflectionRepository,
      ReflectionSummaryRepository reflectionSummaryRepository,
      VectorStore vectorStore,
      ChatModel chatModel) {
    this.reflectionRepository = reflectionRepository;
    this.reflectionSummaryRepository = reflectionSummaryRepository;
    this.vectorStore = vectorStore;
    this.chatModel = chatModel;
  }

  @Async
  @TransactionalEventListener
  public void createReflectionSummary(ReflectionCreatedEvent event) {
    log.debug("Handling ReflectionCreatedEvent {}", event.getReflectionId());

    if (event.getContent().trim().length() <= MINIMUM_SUMMARIZABLE_CONTENT_LENGTH) {
      log.debug("Skipping summary for short reflection {}", event.getReflectionId());
      return;
    }

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);
    Message systemMessage = systemPromptTemplate.createMessage();
    Message userMessage = UserMessage.builder().text(event.getContent()).build();

    Prompt prompt = Prompt.builder().messages(systemMessage, userMessage).build();

    log.debug("Summarizing reflection {}", event.getReflectionId());
    ReflectionSummaryDto response = ChatClient.create(chatModel).prompt(prompt).call()
        .entity(ReflectionSummaryDto.class);
    log.debug("Summarized reflection {}", event.getReflectionId());

    ReflectionSummary summary = ReflectionSummary.builder()
        .reflection(reflectionRepository.getReferenceById(event.getReflectionId()))
        .summary(response.summary())
        .build();

    reflectionSummaryRepository.save(summary);
    log.debug("Saved summary for reflection {}", event.getReflectionId());
  }

  @Async
  @TransactionalEventListener
  public void createReflectionVector(ReflectionCreatedEvent event) {
    log.debug("Creating vector for reflection {}", event.getReflectionId());

    Document document = Document.builder().text(event.getContent()).build();

    vectorStore.add(List.of(document));

    log.debug("Created vector for reflection {}", event.getReflectionId());
  }
}
