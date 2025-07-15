package com.ahmadda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AhMaddaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AhMaddaApplication.class, args);
    }

}
