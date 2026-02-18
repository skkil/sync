package com.skkil.sync.provider.model;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contests")
@Getter
public class Contest extends Provider {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_provider_id", nullable = false)
  @Setter
  private Provider hostProvider;

  protected Contest() {}

  @Builder
  public Contest(String name, String description, String contactInfo, String oneLineReview) {
    super(ProviderType.CONTEST, name, description, contactInfo, oneLineReview);
  }
}
