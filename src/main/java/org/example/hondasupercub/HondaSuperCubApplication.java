package org.example.hondasupercub;

import org.example.hondasupercub.config.WebAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.SecurityConfig;

@SpringBootApplication
public class HondaSuperCubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HondaSuperCubApplication.class, args);
    }

}
