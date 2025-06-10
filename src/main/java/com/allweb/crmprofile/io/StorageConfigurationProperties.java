package com.allweb.crmprofile.io;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageConfigurationProperties {
  private String basePath = Path.of("").toAbsolutePath().toString();

  public String getBasePath() {
    return basePath;
  }

  public Path resolve(String... next) {
    return Path.of(basePath, next);
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
}
