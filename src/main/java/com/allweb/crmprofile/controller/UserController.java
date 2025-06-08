package com.allweb.crmprofile.controller;

import com.allweb.crmprofile.client.OAuth2Client;
import com.allweb.crmprofile.payload.User;
import com.allweb.crmprofile.service.UserService;
import com.allweb.crmprofile.validation.OnCreate;
import com.crm.commons.specification.utils.GenericHashMap;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Mono<User>> createUser(
      @Validated(value = OnCreate.class) @ModelAttribute User user,
      @RequestPart FilePart profile) {
    return ResponseEntity.ok(userService.createUser(profile, user));
  }

  @GetMapping
  public ResponseEntity<Flux<User>> getUsers(
      @RequestParam(value = "filter", required = false) String filter) {
    return ResponseEntity.ok(userService.getUsers(filter));
  }

  @GetMapping("/principleIds/{ids}")
  public ResponseEntity<Flux<User>> getUserByIds(@PathVariable("ids") List<String> ids) {
    return ResponseEntity.ok(userService.getUserByIds(ids));
  }

  @GetMapping("/profile/{filename}")
  public ResponseEntity<Flux<DataBuffer>> read(@PathVariable String filename) {

    ContentDisposition contentDisposition =
        ContentDisposition.attachment().filename(filename).build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(contentDisposition);

    return ResponseEntity.ok().headers(headers).body(userService.readProfile(filename));
  }
}
