package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import com.skkil.sync.user.model.User;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class PostContentMediaServiceTests {

  @Mock private MediaDomainService mediaService;
  @Mock private PostMediaFileRepository postMediaFileRepository;

  private JsonMapper objectMapper;
  private PostContentMediaService contentMediaService;

  @BeforeEach
  void setUp() {
    objectMapper = new JsonMapper();
    contentMediaService =
        new PostContentMediaService(mediaService, objectMapper, postMediaFileRepository);
  }

  @Test
  @DisplayName("[prepareContentForCreate] image media를 업로드 상태로 변경하고 저장 content의 src를 제거")
  void prepareContentForCreate_imageNodes_markMediaUploadedAndClearSources() throws Exception {
    Long authorId = 1L;
    Media firstImage = createMedia(10L);
    Media secondImage = createMedia(11L);

    when(mediaService.getUnlinkedMedia(authorId, 10L)).thenReturn(firstImage);
    when(mediaService.getUnlinkedMedia(authorId, 11L)).thenReturn(secondImage);

    PostContentMediaService.PreparedContent preparedContent =
        contentMediaService.prepareContentForCreate(authorId, contentWithImageSources());

    assertThat(preparedContent.mediaFiles()).containsExactly(firstImage, secondImage);
    assertThat(firstImage.getStatus()).isEqualTo(MediaStatus.UPLOADED);
    assertThat(secondImage.getStatus()).isEqualTo(MediaStatus.UPLOADED);
    assertThat(imageSources(preparedContent.content())).containsExactly("", "", "");
  }

  @Test
  @DisplayName("[resolveImageUrls] post에 연결된 media URL을 image src에 주입")
  void resolveImageUrls_mediaFilesExist_applyPublicUrls() throws Exception {
    Long postId = 1L;
    Post post =
        Post.builder().slug("test-post").author(new User(1L)).title(null).content("").build();
    post.setId(postId);
    Media firstImage = createMedia(10L);
    Media secondImage = createMedia(11L);
    URL firstUrl = URI.create("https://example.com/images/first.jpg").toURL();
    URL secondUrl = URI.create("https://example.com/images/second.jpg").toURL();

    when(postMediaFileRepository.findAllByPostIdOrderBySortOrderAsc(postId))
        .thenReturn(
            List.of(
                new PostMediaFile(post, firstImage, 0), new PostMediaFile(post, secondImage, 1)));
    when(mediaService.generatePublicGetUrls(List.of(10L, 11L)))
        .thenReturn(Map.of(10L, firstUrl, 11L, secondUrl));

    String resolvedContent = contentMediaService.resolveImageUrls(postId, contentForResolve());

    assertThat(imageSources(resolvedContent))
        .containsExactly(firstUrl.toExternalForm(), secondUrl.toExternalForm());
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

  private String contentWithImageSources() {
    return """
        {
          "type": "doc",
          "content": [
            {
              "type": "paragraph",
              "content": [
                {
                  "type": "text",
                  "text": "Post content"
                }
              ]
            },
            {
              "type": "image",
              "attrs": {
                "src": "blob:http://localhost/first",
                "alt": "first.jpg",
                "mediaId": 10
              }
            },
            {
              "type": "image",
              "attrs": {
                "src": "blob:http://localhost/second",
                "alt": "second.jpg",
                "mediaId": 11
              }
            },
            {
              "type": "image",
              "attrs": {
                "src": "blob:http://localhost/duplicate",
                "alt": "duplicate.jpg",
                "mediaId": 10
              }
            }
          ]
        }
        """;
  }

  private String contentForResolve() {
    return """
        {
          "type": "doc",
          "content": [
            {
              "type": "image",
              "attrs": {
                "src": "",
                "alt": "first.jpg",
                "mediaId": 10
              }
            },
            {
              "type": "image",
              "attrs": {
                "src": "",
                "alt": "second.jpg",
                "mediaId": 11
              }
            }
          ]
        }
        """;
  }

  private List<String> imageSources(String content) throws Exception {
    List<String> sources = new ArrayList<>();
    collectImageSources(objectMapper.readTree(content), sources);
    return sources;
  }

  private void collectImageSources(JsonNode node, List<String> sources) {
    if (node == null) {
      return;
    }

    JsonNode type = node.get("type");
    if (node.isObject() && type != null && "image".equals(type.asString())) {
      sources.add(node.get("attrs").get("src").asString());
    }

    JsonNode children = node.get("content");
    if (children != null && children.isArray()) {
      for (JsonNode child : children.values()) {
        collectImageSources(child, sources);
      }
    }
  }
}
