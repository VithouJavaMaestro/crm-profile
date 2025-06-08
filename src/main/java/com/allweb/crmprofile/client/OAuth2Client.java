package com.allweb.crmprofile.client;

import com.allweb.crmprofile.annotation.Client;
import com.allweb.crmprofile.payload.OidcUser;
import com.crm.commons.specification.utils.GenericHashMap;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Client
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class OAuth2Client {

  private final OAuth2ClientProperties properties;

  private final WebClient webClient;

  private Mono<GenericHashMap> oidcCache;

  private Mono<GenericHashMap> oauth2TokenCache;

  private final Duration clockSkew =
      Duration.ofSeconds(TimeUnit.SECONDS.convert(60, TimeUnit.MILLISECONDS));

  public OAuth2Client(OAuth2ClientProperties properties, WebClient webClient) {
    this.properties = properties;
    this.webClient = webClient;
  }

  public void createUser() {}

  public Mono<OidcUser> createOidcUser(OidcUser oidcUser) {
    return getOAuth2AccessToken()
        .map(genericHashMap -> genericHashMap.get("access_token"))
        .cast(String.class)
        .flatMap(
            accessToken ->
                webClient
                    .post()
                    .uri(
                        properties.getIssuerEndpoint(),
                        uriBuilder -> uriBuilder.path("/users").build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .bodyValue(oidcUser)
                    .retrieve()
                    .bodyToMono(OidcUser.class));
  }

  public Mono<GenericHashMap> getOAuth2AccessToken() {
    return getOidc()
        .map(genericHashMap -> genericHashMap.get("token_endpoint", String.class))
        .flatMap(
            tokenEndpoint -> {
              BodyInserters.FormInserter<String> body = buildTokenRequestBody();
              return getOAuth2Token(tokenEndpoint, body);
            });
  }

  private BodyInserters.FormInserter<String> buildTokenRequestBody() {
    return BodyInserters.fromFormData("client_id", properties.getClientId())
        .with("grant_type", "client_credentials")
        .with("client_secret", properties.getClientSecret());
  }

  private Mono<GenericHashMap> getOAuth2Token(
      String tokenEndpoint, BodyInserters.FormInserter<String> body) {
    if (this.oauth2TokenCache == null) {
      this.oauth2TokenCache =
          webClient
              .post()
              .uri(tokenEndpoint)
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(body)
              .retrieve()
              .bodyToMono(GenericHashMap.class)
              .cacheInvalidateIf(this::calculateInvalid);
    }
    return oauth2TokenCache;
  }

  private boolean calculateInvalid(GenericHashMap genericHashMap) {
    int expiresIn = genericHashMap.get("expires_in", Integer.class);
    Instant expiresAt = genericHashMap.get("expires_at", Instant.class);
    if (expiresAt == null) {
      expiresAt = Instant.now().plusMillis(expiresIn);
      genericHashMap.put("expires_at", expiresAt);
    }
    return hasTokenExpired(expiresAt);
  }

  private Mono<GenericHashMap> getOidc() {
    if (this.oidcCache == null) {
      this.oidcCache =
          webClient
              .get()
              .uri(
                  properties.getIssuerEndpoint(),
                  uriBuilder -> uriBuilder.path("/.well-known/openid-configuration").build())
              .retrieve()
              .bodyToMono(GenericHashMap.class)
              .cache();
    }
    return oidcCache;
  }

  private boolean hasTokenExpired(Instant expiresAt) {
    return Instant.now().isAfter(expiresAt.minus(this.clockSkew));
  }
}
