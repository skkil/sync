package com.skkil.sync.post.service;

import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.constants.PostPreviewProperties;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

  public Map<Long, List<MediaDto>> getPreviewMediaForPosts(List<Long> postIds) {
    if (postIds.isEmpty()) {
      return Map.of();
    }

    List<PostMediaFile> postMediaFiles =
        postMediaFileRepository.findByPostIdInAndSortOrderLessThanOrderByPostIdAscSortOrderAsc(
            postIds, PostPreviewProperties.PREVIEW_MEDIA_MAX_COUNT);

    Map<Long, URL> urls =
        mediaService.generatePresignedGetUrls(
            postMediaFiles.stream().map(PostMediaFile::getMedia).toList());

    return postMediaFiles.stream()
        .collect(
            Collectors.groupingBy(
                postMediaFile -> postMediaFile.getPost().getId(),
                Collectors.mapping(
                    postMediaFile ->
                        MediaDto.builder()
                            .id(postMediaFile.getMedia().getId())
                            .url(urls.get(postMediaFile.getMedia().getId()).toExternalForm())
                            .build(),
                    Collectors.toList())));
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

  public List<Media> resolveMediaFilesForUpdate(Long authorId, Long postId, List<Long> mediaIds) {
    if (mediaIds == null || mediaIds.isEmpty()) {
      return List.of();
    }

    Map<Long, Media> currentMedia =
        postMediaFileRepository.findAllByPostIdOrderBySortOrderAsc(postId).stream()
            .map(PostMediaFile::getMedia)
            .collect(Collectors.toMap(Media::getId, media -> media));

    List<Media> mediaFiles = new ArrayList<>();
    for (Long mediaId : new LinkedHashSet<>(mediaIds)) {
      Media media = currentMedia.get(mediaId);
      if (media == null) {
        media = mediaService.getUnlinkedMedia(authorId, mediaId);
        media.markAsUploaded();
      }
      mediaFiles.add(media);
    }

    return mediaFiles;
  }

  public void replaceMediaFiles(Post post, List<Media> mediaFiles) {
    postMediaFileRepository.deleteAllByPostId(post.getId());
    savePostMediaFiles(post, mediaFiles);
  }

  public void savePostMediaFiles(Post post, List<Media> mediaFiles) {
    for (int i = 0; i < mediaFiles.size(); i++) {
      postMediaFileRepository.save(new PostMediaFile(post, mediaFiles.get(i), i));
    }
  }
}
