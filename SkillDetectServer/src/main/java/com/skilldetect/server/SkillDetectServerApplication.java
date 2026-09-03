package com.skilldetect.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.skilldetect.server.config.ScanProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ScanProperties.class)
@MapperScan({"com.skilldetect.server.scan.mapper", "com.skilldetect.server.health.mapper"})
public class SkillDetectServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillDetectServerApplication.class, args);
    }
}
