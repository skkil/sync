package com.skkil.sync.post.model;

import com.skkil.sync.project.model.Project;

public enum PostScope {
  PUBLIC,
  WORKSPACE;

  public static PostScope fromProject(Project project) {
    return project == null ? PUBLIC : WORKSPACE;
  }
}
