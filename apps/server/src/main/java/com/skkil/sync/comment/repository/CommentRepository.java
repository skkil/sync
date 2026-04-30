package com.skkil.sync.comment.repository;

import com.skkil.sync.comment.enums.CommentTargetType;
import com.skkil.sync.comment.model.Comment;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @EntityGraph(attributePaths = "author")
  @Query("""
      SELECT c
      FROM Comment c
      WHERE c.targetType = :targetType
        AND c.targetId = :targetId
        AND c.parent IS NULL
        AND (c.createdAt > :createdAt OR (c.createdAt = :createdAt AND c.id > :id))
      ORDER BY c.createdAt ASC, c.id ASC
      """)
  List<Comment> findRootCommentsAfter(
      @Param("targetType") CommentTargetType targetType,
      @Param("targetId") Long targetId,
      @Param("createdAt") Instant createdAt,
      @Param("id") Long id,
      Pageable pageable);

  @EntityGraph(attributePaths = "author")
  List<Comment> findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAscIdAsc(
      CommentTargetType targetType, Long targetId, Pageable pageable);

  @EntityGraph(attributePaths = "author")
  List<Comment> findByParentInOrderByCreatedAtAscIdAsc(Collection<Comment> parents);
}
