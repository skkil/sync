package com.skkil.sync.comment.repository;

import com.skkil.sync.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
