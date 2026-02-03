package com.skkil.sync.lab.dto.response;

public record LabSearchResponse(
    Long id, String name, String oneLineReview, String schoolName, String professorName) {}
