package com.skkil.sync.post.service;

import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PostContentMediaService {

  private final MediaDomainService mediaService;
  private final PostMediaFileRepository postMediaFileRepository;

  public PostContentMediaService(
      MediaDomainService mediaService, PostMediaFileRepository postMediaFileRepository) {
    this.mediaService = mediaService;
    this.postMediaFileRepository = postMediaFileRepository;
  }

  public List<MediaDto> getMediaFilesForPost(Long postId) {
    List<Media> medias =
        postMediaFileRepository.findAllByPostIdOrderBySortOrderAsc(postId).stream()
            .map(PostMediaFile::getMedia)
            .toList();

    Map<Long, URL> urls = mediaService.generatePresignedGetUrls(medias);

    return medias.stream()
        .map(
            m -> MediaDto.builder().id(m.getId()).url(urls.get(m.getId()).toExternalForm()).build())
        .toList();
  }

  public List<Media> resolveMediaFilesForCreate(Long authorId, List<Long> mediaIds) {
    if (mediaIds == null || mediaIds.isEmpty()) {
      return List.of();
    }

    List<Media> mediaFiles = new ArrayList<>();
    for (Long mediaId : mediaIds) {
      Media media = mediaService.getUnlinkedMedia(authorId, mediaId);
      media.markAsUploaded();
      mediaFiles.add(media);
    }

    return mediaFiles;
  }
}
