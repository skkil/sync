package com.skkil.sync.like.repository;

import com.skkil.sync.like.enums.LikeTargetType;
import com.skkil.sync.like.model.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

  boolean existsByUserIdAndTargetTypeAndTargetId(
      Long userId, LikeTargetType targetType, Long targetId);

  Optional<Like> findByUserIdAndTargetTypeAndTargetId(
      Long userId, LikeTargetType targetType, Long targetId);
}
