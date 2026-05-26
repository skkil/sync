package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.event.ReflectionCreatedEvent;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.model.ReflectionEmbedding;
import com.skkil.sync.reflection.repository.ReflectionEmbeddingRepository;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class ReflectionEmbeddingService {

  private final EmbeddingModel embeddingModel;
  private final ReflectionRepository reflectionRepository;
  private final ReflectionEmbeddingRepository embeddingRepository;

  public ReflectionEmbeddingService(
      EmbeddingModel embeddingModel,
      ReflectionRepository reflectionRepository,
      ReflectionEmbeddingRepository embeddingRepository) {
    this.embeddingModel = embeddingModel;
    this.reflectionRepository = reflectionRepository;
    this.embeddingRepository = embeddingRepository;
  }

  @Async
  @TransactionalEventListener
  public void createReflectionEmbeddings(ReflectionCreatedEvent event) {
    Reflection reflection = reflectionRepository.getReferenceById(event.getReflectionId());

    Document document = Document.builder().text(event.getContent()).build();
    float[] embedding = embeddingModel.embed(document);

    ReflectionEmbedding reflectionEmbedding =
        ReflectionEmbedding.builder().reflection(reflection).embedding(embedding).build();
    embeddingRepository.save(reflectionEmbedding);
  }

  public float[] computeEmbedding(String content) {
    Document document = Document.builder().text(content).build();
    return embeddingModel.embed(document);
  }
}
