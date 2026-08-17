package com.skkil.sync.media.service.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.enums.MediaType;
import com.skkil.sync.media.exception.MediaNotFoundException;
import com.skkil.sync.media.exception.MediaNotUploadedException;
import com.skkil.sync.media.exception.MediaTooLargeException;
import com.skkil.sync.media.exception.UnsupportedMediaTypeException;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import com.skkil.sync.user.model.User;
import io.awspring.cloud.s3.S3Template;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MediaDomainServiceTests {

  private static final Long UPLOADER_ID = 1L;
  private static final Long MEDIA_ID = 10L;

  @Mock private MediaRepository mediaRepository;
  @Mock private S3Template s3Template;
  @Mock private S3Client s3Client;

  @Test
  @DisplayName("[linkMedia] 업로드된 객체가 신고된 내용과 일치하면 UPLOADED로 바꾼다")
  void linkMedia_objectMatchesDeclaredMedia_marksUploaded() {
    givenPendingMedia("image/png", 1024L);
    givenStoredObject("image/png", 1024L);

    Media linked = service().linkMedia(UPLOADER_ID, MEDIA_ID);

    assertThat(linked.getStatus()).isEqualTo(MediaStatus.UPLOADED);
  }

  @Test
  @DisplayName("[linkMedia] S3에 객체가 없으면 연결하지 않는다")
  void linkMedia_objectMissing_throws() {
    givenPendingMedia("image/png", 1024L);
    when(s3Client.headObject(any(HeadObjectRequest.class)))
        .thenThrow(NoSuchKeyException.builder().message("missing").build());

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(MediaNotUploadedException.class);
  }

  @Test
  @DisplayName("[linkMedia] 신고된 크기와 무관하게 실제 객체가 제한을 넘으면 거부한다")
  void linkMedia_storedObjectExceedsCap_throws() {
    Media media = givenPendingMedia("image/png", 1024L);
    givenStoredObject("image/png", MediaType.IMAGE_PNG.maxFileSizeBytes() + 1);

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(MediaTooLargeException.class);
    assertThat(media.getStatus()).isEqualTo(MediaStatus.PENDING);
  }

  @ParameterizedTest
  @ValueSource(strings = {"text/html", "image/png"})
  @DisplayName("[linkMedia] 실제 객체의 Content-Type이 신고된 것과 다르면 거부한다")
  void linkMedia_storedContentTypeOfAnotherMediaType_throws(String storedContentType) {
    givenPendingMedia("application/pdf", 1024L);
    givenStoredObject(storedContentType, 1024L);

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(UnsupportedMediaTypeException.class);
  }

  @Test
  @DisplayName("[linkMedia] 같은 종류라도 Content-Type이 정확히 일치하지 않으면 거부한다")
  void linkMedia_storedContentTypeOfAnotherImageType_throws() {
    givenPendingMedia("image/png", 1024L);
    givenStoredObject("image/svg+xml", 1024L);

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(UnsupportedMediaTypeException.class);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"application/octet-stream", "binary/octet-stream"})
  @DisplayName("[linkMedia] Content-Type은 서명에 포함되므로 없거나 일반적인 값이면 거부한다")
  void linkMedia_storedContentTypeMissingOrGeneric_throws(String storedContentType) {
    Media media = givenPendingMedia("application/pdf", 1024L);
    givenStoredObject(storedContentType, 1024L);

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(UnsupportedMediaTypeException.class);
    assertThat(media.getStatus()).isEqualTo(MediaStatus.PENDING);
  }

  @Test
  @DisplayName("[linkMedia] 업로더가 아니면 연결할 수 없다")
  void linkMedia_requesterIsNotUploader_throws() {
    givenPendingMedia("image/png", 1024L);

    assertThatThrownBy(() -> service().linkMedia(999L, MEDIA_ID))
        .isInstanceOf(MediaNotFoundException.class);
  }

  @Test
  @DisplayName("[linkMedia] 이미 연결된 미디어는 다시 연결할 수 없다")
  void linkMedia_alreadyUploaded_throws() {
    Media media = givenPendingMedia("image/png", 1024L);
    media.markAsUploaded();

    assertThatThrownBy(() -> service().linkMedia(UPLOADER_ID, MEDIA_ID))
        .isInstanceOf(MediaNotFoundException.class);
  }

  @Test
  @DisplayName("[generatePresignedGetUrlsLenient] 서명에 실패한 미디어만 빼고 계속 간다")
  void generatePresignedGetUrlsLenient_skipsFailedItems() throws Exception {
    Media healthy = uploadedMedia(10L, "healthy-key");
    Media broken = uploadedMedia(11L, "broken-key");
    URL url = URI.create("https://cdn.example.com/healthy").toURL();
    when(s3Template.createSignedGetURL(eq("test-bucket"), eq("healthy-key"), any(Duration.class)))
        .thenReturn(url);
    when(s3Template.createSignedGetURL(eq("test-bucket"), eq("broken-key"), any(Duration.class)))
        .thenThrow(new IllegalStateException("자격증명 해소 실패"));

    Map<Long, URL> result =
        service().generatePresignedGetUrlsLenient(List.of(healthy, broken), Function.identity());

    assertThat(result).containsOnlyKeys(10L).containsEntry(10L, url);
  }

  @Test
  @DisplayName("[generatePresignedGetUrlsLenient] UPLOADED가 아닌 미디어도 예외 없이 건너뛴다")
  void generatePresignedGetUrlsLenient_skipsNonUploadedMedia() {
    Media pending = givenPendingMedia("image/png", 1024L);

    Map<Long, URL> result =
        service().generatePresignedGetUrlsLenient(List.of(pending), Function.identity());

    assertThat(result).isEmpty();
  }

  private Media uploadedMedia(Long id, String key) {
    Media media =
        Media.builder()
            .uploader(new User(UPLOADER_ID))
            .mediaType("image/png")
            .bucket("test-bucket")
            .key(key)
            .fileName("test-file")
            .fileSize(1024L)
            .build();
    media.setId(id);
    media.markAsUploaded();
    return media;
  }

  private MediaDomainService service() {
    return new MediaDomainService(mediaRepository, s3Template, s3Client);
  }

  private Media givenPendingMedia(String mediaType, Long fileSize) {
    Media media =
        Media.builder()
            .uploader(new User(UPLOADER_ID))
            .mediaType(mediaType)
            .bucket("test-bucket")
            .key("test-key")
            .fileName("test-file")
            .fileSize(fileSize)
            .build();
    media.setId(MEDIA_ID);

    when(mediaRepository.findById(MEDIA_ID)).thenReturn(Optional.of(media));

    return media;
  }

  private void givenStoredObject(String contentType, Long contentLength) {
    when(s3Client.headObject(any(HeadObjectRequest.class)))
        .thenReturn(
            HeadObjectResponse.builder()
                .contentType(contentType)
                .contentLength(contentLength)
                .build());
  }
}
