package com.healthcare.readmission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.healthcare.readmission.auth.entity",
        "com.healthcare.readmission.model.entity",
        "com.healthcare.readmission.audit.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.healthcare.readmission.auth.repository",
        "com.healthcare.readmission.model.repository",
        "com.healthcare.readmission.audit.repository"
})
public class Application {

    public static void main(String[] args) {
        System.out.println("\n=== Starting Patient Readmission Prediction API ===\n");
        SpringApplication.run(Application.class, args);
    }
}
