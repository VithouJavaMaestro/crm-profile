package com.allweb.crmprofile.service;

import com.allweb.crmprofile.payload.UserIdentity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserIdentityService {

  public Mono<UserIdentity> getUserIdentity() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(JwtAuthenticationToken.class::isInstance)
        .cast(JwtAuthenticationToken.class)
        .map(
            jwtAuthenticationToken -> {
              Jwt jwt = jwtAuthenticationToken.getToken();
              UserIdentity userIdentity = new UserIdentity();
              userIdentity.setAccessToken(jwt.getTokenValue());
              userIdentity.setId(jwt.getSubject());
              return userIdentity;
            });
  }
}
