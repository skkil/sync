package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.user.constant.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Getter
public class User extends BaseEntity {

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "hashed_password", nullable = true, length = 255)
  @Setter
  private String hashedPassword;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_image_id")
  @Setter
  private Media profileImage;

  @Column(name = "full_name", nullable = false, length = 255)
  @Setter
  private String fullName;

  @Column(name = "bio", columnDefinition = "TEXT")
  @Setter
  private String bio;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @Setter
  private Role role = Role.USER;

  @Column(name = "deleted_at", nullable = true)
  private Instant deletedAt;

  @OneToMany(
      mappedBy = "user",
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
      orphanRemoval = true)
  private List<UserOAuth2Account> oAuth2Accounts = new ArrayList<>();

  protected User() {}

  @Builder
  public User(String email, String hashedPassword, String fullName, String bio) {
    this.email = email;
    this.hashedPassword = hashedPassword;
    this.fullName = fullName;
    this.bio = bio;
  }
}
