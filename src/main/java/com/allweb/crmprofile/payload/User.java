package com.allweb.crmprofile.payload;

import com.allweb.crmprofile.validation.OnCreate;
import com.allweb.crmprofile.validation.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class User {

  private Long id;

  @NotEmpty(message = "firstname is mandatory", groups = OnCreate.class)
  private String firstname;

  @NotEmpty(message = "lastname is mandatory", groups = OnCreate.class)
  private String lastname;

  @Email(
      message = "email is invalid",
      groups = {OnCreate.class, OnUpdate.class})
  private String email;

  @Pattern(
      regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$",
      message = "invalid password",
      groups = {OnCreate.class, OnUpdate.class})
  private String password;

  @Pattern(
      regexp = "(^[+]\\d+(?:[ ]\\d+)*)",
      message = "invalid phone number",
      groups = {OnCreate.class, OnUpdate.class})
  private String phone;

  @Pattern(
      regexp =
          "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}",
      message = "invalid profile",
      groups = {OnCreate.class, OnUpdate.class})
  private String profile;

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }
}
