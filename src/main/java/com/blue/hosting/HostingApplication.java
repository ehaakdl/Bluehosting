package com.blue.hosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories("com.blue.hosting.entity")
@EnableTransactionManagement
public class HostingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostingApplication.class, args);
	}
}
