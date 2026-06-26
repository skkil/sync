package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostReport;
import com.skkil.sync.post.model.PostReportStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

  boolean existsByPostIdAndReporterId(Long postId, Long reporterId);

  Page<PostReport> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

  Page<PostReport> findByStatusOrderByCreatedAtDescIdDesc(
      PostReportStatus status, Pageable pageable);

  List<PostReport> findByPostIdAndStatus(Long postId, PostReportStatus status);
}
