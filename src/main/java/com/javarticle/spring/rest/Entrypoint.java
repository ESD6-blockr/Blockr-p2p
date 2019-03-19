package com.javarticle.spring.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Entrypoint {
    
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Entrypoint.class, args);
	}
	

}
