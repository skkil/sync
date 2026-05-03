package com.skkil.sync.comment.repository;

import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.reflection.model.Reflection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @EntityGraph(attributePaths = {"author", "author.profileImage"})
  List<Comment> findByReflection(Reflection reflection);
}
