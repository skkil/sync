package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import com.skkil.sync.user.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostContentMediaServiceTests {

  @Mock private MediaDomainService mediaService;
  @Mock private PostMediaFileRepository postMediaFileRepository;

  private PostContentMediaService contentMediaService;

  @Test
  @DisplayName("[resolveMediaFilesForCreate] mediaId 목록에 해당하는 media를 업로드 상태로 변경")
  void resolveMediaFilesForCreate_mediaIdsGiven_markMediaUploaded() {
    contentMediaService = new PostContentMediaService(mediaService, postMediaFileRepository);

    Long authorId = 1L;
    Media firstImage = createMedia(10L);
    Media secondImage = createMedia(11L);

    when(mediaService.getUnlinkedMedia(authorId, 10L)).thenReturn(firstImage);
    when(mediaService.getUnlinkedMedia(authorId, 11L)).thenReturn(secondImage);

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForCreate(authorId, List.of(10L, 11L));

    assertThat(mediaFiles).containsExactly(firstImage, secondImage);
    assertThat(firstImage.getStatus()).isEqualTo(MediaStatus.UPLOADED);
    assertThat(secondImage.getStatus()).isEqualTo(MediaStatus.UPLOADED);
  }

  @Test
  @DisplayName("[resolveMediaFilesForCreate] mediaId 목록이 비어있으면 빈 목록 반환")
  void resolveMediaFilesForCreate_noMediaIds_returnsEmptyList() {
    contentMediaService = new PostContentMediaService(mediaService, postMediaFileRepository);

    List<Media> mediaFiles = contentMediaService.resolveMediaFilesForCreate(1L, List.of());

    assertThat(mediaFiles).isEmpty();
  }

  private Media createMedia(Long id) {
    Media media =
        Media.builder()
            .uploader(new User(1L))
            .mediaType("image/jpeg")
            .bucket("test-bucket")
            .key("test-key-" + id)
            .fileName("test-" + id + ".jpg")
            .fileSize(100L)
            .build();
    media.setId(id);
    return media;
  }
}
