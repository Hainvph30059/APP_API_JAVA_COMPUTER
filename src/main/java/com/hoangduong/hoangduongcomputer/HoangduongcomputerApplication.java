package com.hoangduong.hoangduongcomputer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HoangduongcomputerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoangduongcomputerApplication.class, args);
    }
}
