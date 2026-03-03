package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserFollowRelationshipRepository
    extends JpaRepository<UserFollowRelationship, Long> {

  @EntityGraph(attributePaths = {"followee"})
  List<UserFollowRelationship> findByFollower(User follower);

  @Query(
      """
      SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
      FROM UserFollowRelationship r
      WHERE r.follower.id = :followerId AND r.followee.id = :followeeId
      """)
  public boolean existsByFollowerAndFollowee(Long followerId, Long followeeId);

  @Modifying
  @Query(
      """
      DELETE FROM UserFollowRelationship r
      WHERE r.follower.id = :followerId AND r.followee.id = :followeeId
      """)
  public void deleteByFollowerAndFollowee(Long followerId, Long followeeId);
}
