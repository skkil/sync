package com.skkil.sync.provider.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "providers")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Provider {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "name", nullable = false, length = 255)
  protected String name;

  @Column(name = "description", columnDefinition = "TEXT")
  protected String description;

  @Column(name = "is_verified")
  protected Boolean isVerified;
}
