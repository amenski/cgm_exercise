package com.docomodigital.exerciseapi;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ExerciseApiApplication {
    private static final String API_VERSION = "API_VERSION";

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // set UTC timezone so
                                                          // that different servers in
                                                          // different timezones use similar notation
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ExerciseApiApplication.class, args);
        BuildProperties properties = context.getBean(BuildProperties.class);
        System.setProperty(API_VERSION, properties.getVersion());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
