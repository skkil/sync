package com.skkil.sync.lab.exception;

public class LabNotFoundException extends RuntimeException {
  public LabNotFoundException(Long id) {
    super("Lab not found with id: " + id);
  }
}
