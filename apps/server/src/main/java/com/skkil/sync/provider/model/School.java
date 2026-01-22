package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.SchoolType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "schools")
@Getter
public class School extends Provider {

  @Enumerated(EnumType.STRING)
  @Column(name = "school_type", length = 100, nullable = false)
  private SchoolType type;
}
