package com.skkil.sync.media.service;

import com.skkil.sync.media.dto.request.UploadMediaRequest;
import com.skkil.sync.media.dto.response.UploadMediaResponse;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.UserService;
import io.awspring.cloud.s3.S3Template;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaService {

  private final UserService userService;
  private final MediaRepository mediaRepository;
  private final S3Template s3Template;

  public MediaService(
      UserService userService, MediaRepository mediaRepository, S3Template s3Template) {
    this.userService = userService;
    this.mediaRepository = mediaRepository;
    this.s3Template = s3Template;
  }

  @Transactional
  public UploadMediaResponse uploadMedia(Long uploaderId, UploadMediaRequest request) {
    User uploader = userService.getUserReference(uploaderId);
    String bucket = "skkil-sync-media";
    String key = UUID.randomUUID().toString();

    Media media =
        Media.builder()
            .uploader(uploader)
            .mediaType(request.mediaType())
            .bucket(bucket)
            .key(key)
            .fileName(request.fileName())
            .fileSize(request.fileSize())
            .build();
    mediaRepository.save(media);

    URL url = s3Template.createSignedPutURL(bucket, key, Duration.ofMinutes(10));

    return UploadMediaResponse.builder()
        .mediaId(media.getId())
        .uploadUrl(url.toExternalForm())
        .expiresAt(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(10))
        .build();
  }
}
