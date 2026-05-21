package com.skkil.sync.bookmark.mapper;

import com.skkil.sync.bookmark.dto.data.BookmarkedReflectionDto;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedReflectionsResponse;
import java.net.URL;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReflectionBookmarkMapper {

  default GetBookmarkedReflectionsResponse.Post toBookmarkedReflectionResponse(
      BookmarkedReflectionDto dto, Map<Long, URL> profileImageUrls) {
    URL profileImageUrl =
        dto.authorProfileImageId() == null
            ? null
            : profileImageUrls.get(dto.authorProfileImageId());

    GetBookmarkedReflectionsResponse.Author author =
        GetBookmarkedReflectionsResponse.Author.builder()
            .id(dto.authorId())
            .handle(dto.authorHandle())
            .name(dto.authorName())
            .profileImageUrl(profileImageUrl == null ? null : profileImageUrl.toExternalForm())
            .build();

    return GetBookmarkedReflectionsResponse.Post.builder()
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
