package com.docomodigital.exerciseapi;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ExerciseApiApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // set UTC timezone so that different servers in different timezones use the same standard
    }

    public static void main(String[] args) {
        SpringApplication.run(ExerciseApiApplication.class, args);
    }
}
