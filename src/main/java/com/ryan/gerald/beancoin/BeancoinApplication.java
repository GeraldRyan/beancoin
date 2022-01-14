package com.ryan.gerald.beancoin;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class BeancoinApplication {
    public static void main(String[] args) {
        SpringApplication.run(BeancoinApplication.class, args);
    }


    @Bean
    Gson gsonSingletonDeclaration() {
        return new Gson();
    }
}
