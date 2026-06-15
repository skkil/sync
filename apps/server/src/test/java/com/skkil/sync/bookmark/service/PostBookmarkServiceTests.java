package com.skkil.sync.bookmark.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.skkil.sync.bookmark.repository.PostBookmarkRepository;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.service.PostDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostBookmarkServiceTests {

  @Mock private PostBookmarkRepository postBookmarkRepository;
  @Mock private PostDomainService postDomainService;

  @InjectMocks private PostBookmarkService postBookmarkService;

  @Test
  @DisplayName("[bookmarkPost] 회고 북마크를 생성함")
  void bookmarkPost_insertsBookmark() {
    Long userId = 1L;
    Long postId = 2L;

    postBookmarkService.bookmarkPost(userId, postId);

    verify(postDomainService).getPost(postId);
    verify(postBookmarkRepository).insertIfAbsent(userId, postId);
  }

  @Test
  @DisplayName("[bookmarkPost] 존재하지 않는 회고를 북마크하려는 경우 예외 발생")
  void bookmarkPost_postNotFound_throwsException() {
    Long userId = 1L;
    Long postId = 2L;

    doThrow(new PostNotFoundException(postId)).when(postDomainService).getPost(postId);

    assertThatThrownBy(() -> postBookmarkService.bookmarkPost(userId, postId))
        .isInstanceOf(PostNotFoundException.class);

    verify(postBookmarkRepository, never()).insertIfAbsent(userId, postId);
  }

  @Test
  @DisplayName("[unbookmarkPost] 회고 북마크를 삭제함")
  void unbookmarkPost_deletesBookmark() {
    Long userId = 1L;
    Long postId = 2L;

    postBookmarkService.unbookmarkPost(userId, postId);

    verify(postBookmarkRepository).deleteByUser_IdAndPost_Id(userId, postId);
    verifyNoInteractions(postDomainService);
  }
}
