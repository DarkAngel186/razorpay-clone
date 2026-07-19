package com.lp.razorpay_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableScheduling
public class RazorpayCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(RazorpayCloneApplication.class, args);
	}

}
