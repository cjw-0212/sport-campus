package com.example.serviceback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author CJW
 * @since 2024/3/21
 */
@SpringBootApplication
@EnableTransactionManagement
public class ServiceBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBackApplication.class, args);
    }

}
