package com.skilldetect.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.skilldetect.server.config.ScanProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ScanProperties.class)
public class SkillDetectServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillDetectServerApplication.class, args);
    }
}
