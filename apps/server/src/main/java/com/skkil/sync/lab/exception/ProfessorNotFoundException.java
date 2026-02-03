package com.skkil.sync.lab.exception;

public class ProfessorNotFoundException extends RuntimeException {
  public ProfessorNotFoundException(Long id) {
    super("Professor not found with id: " + id);
  }
}
