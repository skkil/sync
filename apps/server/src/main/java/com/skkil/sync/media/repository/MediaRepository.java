package com.skkil.sync.media.repository;

import com.skkil.sync.media.model.Media;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MediaRepository extends JpaRepository<Media, Long> {

  List<Media> findAllByIdIn(List<Long> mediaIds);

  @Modifying
  @Query(
      value =
          """
          UPDATE media_files
          SET status = 'DELETED', updated_at = CURRENT_TIMESTAMP
          WHERE id IN (
            SELECT icon_media_id
            FROM projects
            WHERE id = :projectId AND icon_media_id IS NOT NULL

            UNION

            SELECT cover_media_id
            FROM posts
            WHERE project_id = :projectId AND cover_media_id IS NOT NULL

            UNION

            SELECT pmf.media_id
            FROM post_media_files pmf
            JOIN posts p ON p.id = pmf.post_id
            WHERE p.project_id = :projectId
          )
          """,
      nativeQuery = true)
  void markProjectMediaDeleted(Long projectId);
}
