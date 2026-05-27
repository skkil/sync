package com.skkil.sync.reflection.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Vector;

@Entity
@Table(name = "reflection_embeddings")
@Getter
public class ReflectionEmbedding extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reflection_id", nullable = false)
  private Reflection reflection;

  @Column(name = "embedding", nullable = false, columnDefinition = "vector(1536)")
  @JdbcTypeCode(SqlTypes.VECTOR)
  @Array(length = 1536)
  private Vector embedding;

  protected ReflectionEmbedding() {}

  @Builder
  public ReflectionEmbedding(Reflection reflection, float[] embedding) {
    this.reflection = reflection;
    this.embedding = Vector.of(embedding);
  }
}
