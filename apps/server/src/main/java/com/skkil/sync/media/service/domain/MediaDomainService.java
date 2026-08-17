package com.skkil.sync.media.service.domain;

import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.enums.MediaType;
import com.skkil.sync.media.exception.MediaNotFoundException;
import com.skkil.sync.media.exception.MediaNotUploadedException;
import com.skkil.sync.media.exception.MediaTooLargeException;
import com.skkil.sync.media.exception.UnsupportedMediaTypeException;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import io.awspring.cloud.s3.S3Template;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Service
@Slf4j
public class MediaDomainService {

  private final MediaRepository mediaRepository;
  private final S3Template s3Template;
  private final S3Client s3Client;

  public MediaDomainService(
      MediaRepository mediaRepository, S3Template s3Template, S3Client s3Client) {
    this.mediaRepository = mediaRepository;
    this.s3Template = s3Template;
    this.s3Client = s3Client;
  }

  /**
   * Marks a pending media file as uploaded, after confirming that the object actually exists in S3
   * and that it matches what the uploader declared. Every caller that attaches media to a post,
   * project or profile must go through here — marking media uploaded on the client's word alone
   * leaves rows pointing at objects that were never stored.
   */
  @Transactional
  public Media linkMedia(Long requesterId, Long mediaId) {
    Media media = getUnlinkedMedia(requesterId, mediaId);
    HeadObjectResponse object = headObject(media);

    verifySize(media, object);
    verifyMediaType(media, object);

    media.markAsUploaded();

    return media;
  }

  private HeadObjectResponse headObject(Media media) {
    try {
      return s3Client.headObject(
          HeadObjectRequest.builder().bucket(media.getBucket()).key(media.getKey()).build());
    } catch (NoSuchKeyException exception) {
      log.debug("Media with ID {} has no object at {}.", media.getId(), media.getKey());

      throw new MediaNotUploadedException(media.getId(), exception);
    }
  }

  private void verifySize(Media media, HeadObjectResponse object) {
    long maxFileSize = media.getMediaType().maxFileSizeBytes();
    Long contentLength = object.contentLength();

    if (contentLength != null && contentLength > maxFileSize) {
      log.debug(
          "Media with ID {} was declared as {} bytes but the stored object is {} bytes.",
          media.getId(),
          media.getFileSize(),
          contentLength);

      throw new MediaTooLargeException(contentLength, maxFileSize);
    }
  }

  /**
   * The declared media type decides how the object is served back, so the stored object must carry
   * exactly that content type. {@link com.skkil.sync.media.service.MediaService} signs it into the
   * presigned PUT, which makes any other value unuploadable — this re-checks it because signature
   * enforcement lives in the object store, and a store that does not enforce signed headers (or a
   * URL issued before the content type was pinned) would otherwise leave the type unverified.
   */
  private void verifyMediaType(Media media, HeadObjectResponse object) {
    String contentType = object.contentType();

    boolean matchesDeclaredType =
        MediaType.from(contentType)
            .filter(mediaType -> mediaType == media.getMediaType())
            .isPresent();

    if (!matchesDeclaredType) {
      log.debug(
          "Media with ID {} was declared as {} but the stored object is {}.",
          media.getId(),
          media.getMediaType().getMimeType(),
          contentType);

      throw new UnsupportedMediaTypeException(contentType);
    }
  }

  private Media getUnlinkedMedia(Long requesterId, Long mediaId) {
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
  public Map<Long, URL> generatePresignedGetUrlsByIds(List<Long> mediaIds) {
    List<Media> medias = mediaRepository.findAllByIdIn(mediaIds);

    Map<Long, URL> mediaIdToUrl = new HashMap<>();
    for (Media media : medias) {
      mediaIdToUrl.put(media.getId(), generatePresignedGetUrl(media));
    }

    return mediaIdToUrl;
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

  /**
   * {@link #generatePresignedGetUrls(List, Function)}의 관대한 변형 — 서명에 실패한 미디어는 결과에서 빼고 계속 간다.
   *
   * <p>미디어 URL이 본질이 아닌 소비자(예: 알림의 행위자 아바타)를 위한 것이다. strict 버전은 한 건의 실패가 예외로 전파되는데, 호출자가 쓰기 트랜잭션이면
   * 참여-실패 규칙에 따라 공유 트랜잭션이 rollback-only로 표시된다 — 호출부 try/catch로는 지울 수 없어 본체 저장까지 함께 사라진다. 여기서는 예외가 이
   * 메서드 경계를 벗어나지 않으므로 그 표시 자체가 생기지 않는다.
   *
   * <p>의도적으로 {@code @Transactional}이 없다: 이 메서드는 DB를 읽지 않고(엔티티는 호출자가 이미 로드했고 서명은 로컬 연산이다), 커밋 완료 후의
   * {@code afterCommit} 콜백에서도 호출되는데 그 시점의 트랜잭션 프록시 진입은 "커밋이 뒤따르지 않는 참여"가 되어 Spring이 금지하는 패턴이다
   * ({@code TransactionSynchronization#afterCommit} 계약 참고). 프록시가 없으면 그 문제 자체가 생기지 않는다.
   *
   * <p>S3 서명은 자격증명 체인 해소(EC2 IMDS) 때문에 네트워크 장애로도 실패할 수 있다 — 미디어 상태가 정상이어도 안전하지 않다.
   */
  public <T> Map<Long, URL> generatePresignedGetUrlsLenient(
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

      try {
        result.put(media.getId(), generatePresignedGetUrl(media));
      } catch (RuntimeException exception) {
        log.warn("미디어 {} 서명 실패 — URL 없이 진행한다.", media.getId(), exception);
      }
    }

    return result;
  }
}
