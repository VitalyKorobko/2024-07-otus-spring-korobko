package ru.otus.example.facadegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FacadeGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacadeGatewayApplication.class, args);
	}

}
