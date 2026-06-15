package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSummaryRepository extends JpaRepository<PostSummary, Long> {}
