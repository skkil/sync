package com.skkil.sync.provider.project.repository;

import com.skkil.sync.provider.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {}
