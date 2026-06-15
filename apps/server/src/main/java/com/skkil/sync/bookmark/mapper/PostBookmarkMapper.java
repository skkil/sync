package com.skkil.sync.bookmark.mapper;

import com.skkil.sync.bookmark.dto.data.BookmarkedPostDto;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedPostsResponse;
import java.net.URL;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostBookmarkMapper {

  default GetBookmarkedPostsResponse.Post toBookmarkedPostResponse(
      BookmarkedPostDto dto, Map<Long, URL> profileImageUrls) {
    URL profileImageUrl =
        dto.authorProfileImageId() == null
            ? null
            : profileImageUrls.get(dto.authorProfileImageId());

    GetBookmarkedPostsResponse.Author author =
        GetBookmarkedPostsResponse.Author.builder()
            .id(dto.authorId())
            .handle(dto.authorHandle())
            .name(dto.authorName())
            .profileImageUrl(profileImageUrl == null ? null : profileImageUrl.toExternalForm())
            .build();

    return GetBookmarkedPostsResponse.Post.builder()
        .id(dto.id())
        .slug(dto.slug())
        .author(author)
        .content(dto.content())
        .likeCount(dto.likeCount())
        .commentCount(dto.commentCount())
        .bookmarked(Boolean.TRUE.equals(dto.bookmarked()))
        .createdAt(dto.createdAt())
        .bookmarkedAt(dto.bookmarkedAt())
        .build();
  }
}
