package com.allweb.crmprofile.payload;

import java.util.Map;

public class OidcUser {

  private String id;

  private String username;

  private String password;

  private Map<String, Object> attributes;

  public String getUsername() {
    return username;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }
}
