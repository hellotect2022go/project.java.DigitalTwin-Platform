package com.mpole.hdt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HdtGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HdtGatewayServerApplication.class, args);
	}

}
