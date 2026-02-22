package com.skkil.sync.provider.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "maintainers",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_maintainers_provider_user",
          columnNames = {"provider_id", "user_id"})
    })
@Getter
public class Maintainer extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  private Provider provider;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  protected Maintainer() {}

  @Builder
  public Maintainer(Provider provider, User user) {
    this.provider = provider;
    this.user = user;
  }
}
