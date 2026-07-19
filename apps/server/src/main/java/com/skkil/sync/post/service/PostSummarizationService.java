package com.skkil.sync.post.service;

import com.skkil.sync.post.dto.data.PostSummaryDto;
import com.skkil.sync.post.event.PostContentChangedEvent;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class PostSummarizationService {

  private static final int MINIMUM_SUMMARIZABLE_CONTENT_LENGTH = 200;

  @Value("classpath:/prompts/post/summary.st")
  private Resource resource;

  private final ChatModel chatModel;
  private final PostRepository postRepository;

  @Value("${app.ai.enabled:true}")
  private boolean aiEnabled;

  public PostSummarizationService(ChatModel chatModel, PostRepository postRepository) {
    this.chatModel = chatModel;
    this.postRepository = postRepository;
  }

  @Async
  @TransactionalEventListener
  public void refreshPostSummary(PostContentChangedEvent event) {
    log.debug("Handling PostContentChangedEvent {}", event.getPostId());

    if (!aiEnabled) {
      log.debug("AI features disabled, skipping summary for post {}", event.getPostId());
      return;
    }

    if (event.getContent().trim().length() <= MINIMUM_SUMMARIZABLE_CONTENT_LENGTH) {
      log.debug("Skipping summary for short post {}", event.getPostId());
      updateGeneratedSummary(event.getPostId(), null);
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

    updateGeneratedSummary(event.getPostId(), response.summary());

    log.debug("Saved summary for post {}", event.getPostId());
  }

  /**
   * Persists a system-generated summary (e.g. from {@link #refreshPostSummary}). Unlike {@link
   * PostService#updatePostSummary}, this is not a user-initiated edit, so it is not gated behind
   * the 'EDIT' permission.
   */
  @Transactional
  public void updateGeneratedSummary(Long postId, String summary) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    post.updateSummary(summary);
  }
}
