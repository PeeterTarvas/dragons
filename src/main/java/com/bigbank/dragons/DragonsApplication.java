package com.bigbank.dragons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DragonsApplication {

  public static void main(String[] args) {
    SpringApplication.run(DragonsApplication.class, args);
  }
}
