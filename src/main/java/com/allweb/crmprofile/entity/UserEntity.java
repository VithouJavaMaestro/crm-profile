package com.allweb.crmprofile.entity;

import static com.allweb.crmprofile.constants.UserEntityColumns.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public class UserEntity {

  @Id
  @Column(ID)
  private Long id;

  @Column(FIRSTNAME)
  private String firstname;

  @Column(LASTNAME)
  private String lastname;

  @Column(EMAIL)
  private String email;

  @Column(PROFILE)
  private String profile;

  @Column(PROFILE_PATH)
  private String profilePath;

  @Column(PRINCIPLE_ID)
  private String principleId;

  @Column(PHONE)
  private String phone;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

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
