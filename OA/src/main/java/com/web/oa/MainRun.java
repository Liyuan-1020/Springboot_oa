package com.web.oa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.web.oa.mapper")
public class MainRun {
    public static void main(String[] args) {
        SpringApplication.run(MainRun.class,args);
    }
}