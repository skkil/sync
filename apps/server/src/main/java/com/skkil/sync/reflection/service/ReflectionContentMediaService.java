package com.skkil.sync.reflection.service;

import com.skkil.sync.media.service.domain.MediaDomainService;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@Slf4j
public class ReflectionContentMediaService {

  private static final String ATTRS = "attrs";
  private static final String CONTENT = "content";
  private static final String IMAGE = "image";
  private static final String MEDIA_ID = "mediaId";
  private static final String SRC = "src";
  private static final String TYPE = "type";

  private final MediaDomainService mediaService;
  private final ObjectMapper objectMapper;

  public ReflectionContentMediaService(MediaDomainService mediaService, ObjectMapper objectMapper) {
    this.mediaService = mediaService;
    this.objectMapper = objectMapper;
  }

  public String prepareContentForCreate(Long authorId, String content) {
    JsonNode root = readContent(content);
    Set<Long> mediaIds = getImageMediaIds(root);

    for (Long mediaId : mediaIds) {
      mediaService.getUnlinkedMedia(authorId, mediaId).markAsUploaded();
    }

    clearImageSources(root);
    return writeContent(root);
  }

  public String resolveImageUrls(String content) {
    try {
      JsonNode root = readContent(content);
      Set<Long> mediaIds = getImageMediaIds(root);

      if (mediaIds.isEmpty()) {
        return content;
      }

      Map<Long, URL> urls = mediaService.generatePublicGetUrls(mediaIds.stream().toList());
      applyImageUrls(root, urls);

      return writeContent(root);
    } catch (RuntimeException e) {
      log.debug("Failed to resolve reflection image URLs", e);
      return content;
    }
  }

  private JsonNode readContent(String content) {
    try {
      return objectMapper.readTree(content);
    } catch (JacksonException e) {
      throw new IllegalArgumentException("Invalid reflection content", e);
    }
  }

  private String writeContent(JsonNode content) {
    try {
      return objectMapper.writeValueAsString(content);
    } catch (JacksonException e) {
      throw new IllegalArgumentException("Invalid reflection content", e);
    }
  }

  private Set<Long> getImageMediaIds(JsonNode node) {
    Set<Long> mediaIds = new LinkedHashSet<>();
    collectImageMediaIds(node, mediaIds);
    return mediaIds;
  }

  private void collectImageMediaIds(JsonNode node, Set<Long> mediaIds) {
    if (node == null) {
      return;
    }

    Long mediaId = getImageMediaId(node);
    if (mediaId != null) {
      mediaIds.add(mediaId);
    }

    JsonNode content = node.get(CONTENT);
    if (content != null && content.isArray()) {
      for (JsonNode child : content.values()) {
        collectImageMediaIds(child, mediaIds);
      }
    }
  }

  private Long getImageMediaId(JsonNode node) {
    if (!isImageNode(node)) {
      return null;
    }

    JsonNode attrs = node.get(ATTRS);
    JsonNode mediaId = attrs == null ? null : attrs.get(MEDIA_ID);

    if (mediaId == null) {
      return null;
    }

    long value = mediaId.asLong(-1L);
    return value > 0 ? value : null;
  }

  private boolean isImageNode(JsonNode node) {
    JsonNode type = node.get(TYPE);
    return node.isObject() && type != null && IMAGE.equals(type.asString());
  }

  private void clearImageSources(JsonNode node) {
    updateImageSources(node, Map.of());
  }

  private void applyImageUrls(JsonNode node, Map<Long, URL> urls) {
    updateImageSources(node, urls);
  }

  private void updateImageSources(JsonNode node, Map<Long, URL> urls) {
    if (node == null) {
      return;
    }

    if (isImageNode(node)) {
      Long mediaId = getImageMediaId(node);
      JsonNode attrs = node.get(ATTRS);

      if (attrs instanceof ObjectNode objectAttrs) {
        URL url = mediaId == null ? null : urls.get(mediaId);
        objectAttrs.put(SRC, url == null ? "" : url.toExternalForm());
      }
    }

    JsonNode content = node.get(CONTENT);
    if (content != null && content.isArray()) {
      for (JsonNode child : content.values()) {
        updateImageSources(child, urls);
      }
    }
  }
}
