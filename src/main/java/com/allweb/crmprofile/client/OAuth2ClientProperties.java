package com.allweb.crmprofile.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ClientProperties {
    private String issuerEndpoint;

    private String clientId;

    private String clientSecret;

    public String getIssuerEndpoint() {
        return issuerEndpoint;
    }

    public void setIssuerEndpoint(String issuerEndpoint) {
        this.issuerEndpoint = issuerEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
