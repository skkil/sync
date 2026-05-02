package com.skkil.sync.comment.repository;

import com.skkil.sync.comment.enums.CommentTargetType;
import com.skkil.sync.comment.model.Comment;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @EntityGraph(attributePaths = "author")
  List<Comment> findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAscIdAsc(
      CommentTargetType targetType, Long targetId);

  @EntityGraph(attributePaths = "author")
  List<Comment> findByParentInOrderByCreatedAtAscIdAsc(Collection<Comment> parents);
}
