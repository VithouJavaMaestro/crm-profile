package com.allweb.crmprofile.controller;

import com.allweb.crmprofile.client.OAuth2Client;
import com.allweb.crmprofile.payload.User;
import com.allweb.crmprofile.service.UserService;
import com.allweb.crmprofile.validation.OnCreate;
import com.crm.commons.specification.utils.GenericHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final OAuth2Client oAuth2Client;

  public UserController(UserService userService, OAuth2Client oAuth2Client) {
    this.userService = userService;
    this.oAuth2Client = oAuth2Client;
  }

  @PostMapping
  public ResponseEntity<Mono<User>> createUser(
      @Validated(value = OnCreate.class) @RequestBody User user) {
    return ResponseEntity.ok(userService.createUser(user));
  }

  @GetMapping("/oidc")
  public ResponseEntity<Mono<GenericHashMap>> getOidcEndpoint() {
    return ResponseEntity.ok(this.oAuth2Client.getOAuth2AccessToken());
  }
}
