package com.get_quotes.getQuotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
@ComponentScan(basePackages = {"com.get_quotes.getQuotes",
		"com.get_quotes.getQuotes.Datalayer", "com.get_quotes.getQuotes.Datalayer.Service","com.get_quotes.getQuotes.telegram",
		"com.get_quotes.getQuotes.Datalayer.Pojo", "com.get_quotes.getQuotes.Controller"})
public class GetQuotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetQuotesApplication.class, args);
	}


}
