package com.skkil.sync.media.service.domain;

import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.exception.MediaNotFoundException;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MediaDomainService {

  private final MediaRepository mediaRepository;
  private final S3Template s3Template;

  public MediaDomainService(MediaRepository mediaRepository, S3Template s3Template) {
    this.mediaRepository = mediaRepository;
    this.s3Template = s3Template;
  }

  @Transactional
  public Media getUnlinkedMedia(Long requesterId, Long mediaId) {
    Media media =
        mediaRepository.findById(mediaId).orElseThrow(() -> new MediaNotFoundException(mediaId));

    if (!requesterId.equals(media.getUploader().getId())) {
      log.debug(
          "Requester with ID {} is not the uploader of media with ID {}. Uploader ID: {}",
          requesterId,
          media.getId(),
          media.getUploader().getId());

      throw new MediaNotFoundException(mediaId);
    }

    if (media.getStatus() != MediaStatus.PENDING) {
      log.debug(
          "Media with ID {} is not in a valid state. Current status: {}",
          media.getId(),
          media.getStatus());

      throw new MediaNotFoundException(media.getId());
    }

    return media;
  }

  @Transactional(readOnly = true)
  public URL generatePresignedGetUrl(Media media) {
    if (media.getStatus() != MediaStatus.UPLOADED) {
      log.debug(
          "Media with ID {} is not in a valid state. Current status: {}",
          media.getId(),
          media.getStatus());

      throw new MediaNotFoundException(media.getId());
    }

    return s3Template.createSignedGetURL(media.getBucket(), media.getKey(), Duration.ofMinutes(10));
  }

  @Transactional(readOnly = true)
  public URL generatePublicGetUrl(Media media) {
    if (media.getStatus() != MediaStatus.UPLOADED) {
      log.debug(
          "Media with ID {} is not in a valid state. Current status: {}",
          media.getId(),
          media.getStatus());

      throw new MediaNotFoundException(media.getId());
    }

    try {
      return s3Template.createResource(media.getBucket(), media.getKey()).getURL();
    } catch (IOException e) {
      throw new RuntimeException("Failed to get media URL", e);
    }
  }

  @Transactional(readOnly = true)
  public Map<Long, URL> generatePublicGetUrls(List<Long> mediaIds) {
    List<Media> medias = mediaRepository.findAllByIdIn(mediaIds);

    Map<Long, URL> mediaIdToUrl = new HashMap<>();
    for (Media media : medias) {
      mediaIdToUrl.put(media.getId(), generatePublicGetUrl(media));
    }

    return mediaIdToUrl;
  }

  @Transactional(readOnly = true)
  public <T> Map<Long, URL> generatePublicGetUrls(
      List<T> items, Function<T, Media> mediaExtractor) {
    Map<Long, URL> result = new HashMap<>();
    for (T item : items) {
      if (item == null) {
        continue;
      }

      Media media = mediaExtractor.apply(item);
      if (media == null) {
        continue;
      }

      result.put(media.getId(), generatePublicGetUrl(media));
    }

    return result;
  }

  @Transactional(readOnly = true)
  public <T> Map<Long, URL> generatePresignedGetUrls(List<Media> medias) {
    Map<Long, URL> result = new HashMap<>();
    for (Media media : medias) {
      if (media == null) {
        continue;
      }

      result.put(media.getId(), generatePresignedGetUrl(media));
    }

    return result;
  }

  @Transactional(readOnly = true)
  public <T> Map<Long, URL> generatePresignedGetUrls(
      List<T> items, Function<T, Media> mediaExtractor) {
    Map<Long, URL> result = new HashMap<>();
    for (T item : items) {
      if (item == null) {
        continue;
      }

      Media media = mediaExtractor.apply(item);
      if (media == null) {
        continue;
      }

      result.put(media.getId(), generatePresignedGetUrl(media));
    }

    return result;
  }
}
