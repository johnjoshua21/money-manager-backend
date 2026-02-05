package com.moneymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MoneyManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyManagerApplication.class, args);
    }
}
