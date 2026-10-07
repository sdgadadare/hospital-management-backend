
package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
	    "com.hospital",
	    "controller",
	    "service",
	    "exception"
	})
	@EntityScan(basePackages = "entity")
	@EnableJpaRepositories(basePackages = "repository")
	public class HospitalManagementSystemApplication {

	    public static void main(String[] args) {
	        SpringApplication.run(
	            HospitalManagementSystemApplication.class, args);
	    }
	}


