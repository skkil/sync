package com.skkil.sync.post.listener;

import com.skkil.sync.post.dto.data.PostSummaryDto;
import com.skkil.sync.post.event.PostCreatedEvent;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.repository.PostRepository;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class PostCreatedEventListener {

  private static final int MINIMUM_SUMMARIZABLE_CONTENT_LENGTH = 200;

  @Value("classpath:/prompts/post/summary.st")
  private Resource resource;

  private final PostRepository postRepository;
  private final ChatModel chatModel;

  public PostCreatedEventListener(PostRepository postRepository, ChatModel chatModel) {
    this.postRepository = postRepository;
    this.chatModel = chatModel;
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createPostSummary(PostCreatedEvent event) {
    log.debug("Handling PostCreatedEvent {}", event.getPostId());

    if (event.getContent().trim().length() <= MINIMUM_SUMMARIZABLE_CONTENT_LENGTH) {
      log.debug("Skipping summary for short post {}", event.getPostId());
      return;
    }

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);
    Message systemMessage = systemPromptTemplate.createMessage();
    Message userMessage = UserMessage.builder().text(event.getContent()).build();

    Prompt prompt = Prompt.builder().messages(systemMessage, userMessage).build();

    log.debug("Summarizing post {}", event.getPostId());
    PostSummaryDto response =
        ChatClient.create(chatModel).prompt(prompt).call().entity(PostSummaryDto.class);
    log.debug("Summarized post {}", event.getPostId());

    Post post =
        postRepository
            .findById(event.getPostId())
            .orElseThrow(() -> new PostNotFoundException(event.getPostId()));
    post.updateSummary(response.summary());

    log.debug("Saved summary for post {}", event.getPostId());
  }
}
