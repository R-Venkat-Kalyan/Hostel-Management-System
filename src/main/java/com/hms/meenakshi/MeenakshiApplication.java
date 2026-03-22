package com.hms.meenakshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MeenakshiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeenakshiApplication.class, args);
	}

}
