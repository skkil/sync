package com.skkil.sync.provider.project.repository;

import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamBuildingPostRepository extends JpaRepository<TeamBuildingPost, Long> {

  List<TeamBuildingPost> findByProject(Project project);
}
