package com.ryan.gerald.beancoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwagger2
public class BeancoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeancoinApplication.class, args);
	}

}
