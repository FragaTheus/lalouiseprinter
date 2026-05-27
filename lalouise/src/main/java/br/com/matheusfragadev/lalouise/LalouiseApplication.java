package br.com.matheusfragadev.lalouise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LalouiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LalouiseApplication.class, args);
	}

}
