package com.skkil.sync.project.dto.request;

public record UpdateProjectRequest(
    String description, String website, String iconMediaId, Boolean removeIcon) {}
