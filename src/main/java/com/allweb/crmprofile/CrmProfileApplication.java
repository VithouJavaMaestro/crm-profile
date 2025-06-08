package com.allweb.crmprofile;

import com.allweb.crmprofile.io.StorageConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableConfigurationProperties(StorageConfigurationProperties.class)
public class CrmProfileApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrmProfileApplication.class, args);
  }
}
