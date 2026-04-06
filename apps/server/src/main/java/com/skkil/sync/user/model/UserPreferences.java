package com.skkil.sync.user.model;

import com.skkil.sync.user.enums.Theme;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "user_preferences")
@Getter
public class UserPreferences {

  @Id private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @MapsId
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "theme", nullable = false)
  private Theme theme = Theme.SYSTEM;

  public void setUser(User user) {
    this.user = user;
  }

  public void updateTheme(Theme theme) {
    this.theme = theme;
  }
}
