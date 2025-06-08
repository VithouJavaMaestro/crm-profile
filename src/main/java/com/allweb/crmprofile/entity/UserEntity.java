package com.allweb.crmprofile.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public class UserEntity {
  @Id private Long id;

  private String firstname;

  private String lastname;

  private String email;

  private String profile;

  @Column("profile_path")
  private String profilePath;

  @Column("principle_id")
  private String principleId;

  public String getPrincipleId() {
    return principleId;
  }

  public void setPrincipleId(String principleId) {
    this.principleId = principleId;
  }

  public String getProfile() {
    return profile;
  }

  public String getProfilePath() {
    return profilePath;
  }

  public void setProfilePath(String profilePath) {
    this.profilePath = profilePath;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getId() {
    return id;
  }
}
