package fr.recia.widget.api.api.rest.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.demo_redis.config.bean")
@ComponentScan
public class DemoCasProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoCasProxyApplication.class, args);
	}

}
