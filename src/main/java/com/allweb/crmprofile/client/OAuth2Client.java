package com.allweb.crmprofile.client;

import com.allweb.crmprofile.annotation.Client;
import com.crm.commons.specification.utils.GenericHashMap;
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

  public OAuth2Client(OAuth2ClientProperties properties, WebClient webClient) {
    this.properties = properties;
    this.webClient = webClient;
  }

  public Mono<GenericHashMap> getOidcEndpoint() {
    return oidcCache;
  }

  public Mono<GenericHashMap> getOAuth2AccessToken() {
    return getOidcCache()
        .map(genericHashMap -> genericHashMap.get("token_endpoint", String.class))
        .flatMap(
            tokenEndpoint -> {
              BodyInserters.FormInserter<String> body =
                  BodyInserters.fromFormData("client_id", properties.getClientId())
                      .with("grant_type", "client_credentials")
                      .with("client_secret", properties.getClientSecret());
              return getOAuth2TokenCache(tokenEndpoint, body);
            });
  }

  private Mono<GenericHashMap> getOAuth2TokenCache(
      String tokenEndpoint, BodyInserters.FormInserter<String> body) {
    return Mono.justOrEmpty(oauth2TokenCache)
        .flatMap(genericHashMapMono -> genericHashMapMono)
        .switchIfEmpty(getOAuth2Token(tokenEndpoint, body));
  }

  private Mono<GenericHashMap> getOAuth2Token(
      String tokenEndpoint, BodyInserters.FormInserter<String> body) {
    this.oauth2TokenCache =
        webClient
            .post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .bodyToMono(GenericHashMap.class)
            .cache();
    return oauth2TokenCache;
  }

  private Mono<GenericHashMap> getOidcCache() {
    return Mono.justOrEmpty(oidcCache)
        .switchIfEmpty(doGetOidcEndpoint());
  }

  private Mono<GenericHashMap> doGetOidcEndpoint() {
    this.oidcCache =
        webClient
            .get()
            .uri(properties.getIssuerEndpoint())
            .retrieve()
            .bodyToMono(GenericHashMap.class)
            .cache();
    return oidcCache;
  }
}
