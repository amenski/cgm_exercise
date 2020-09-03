package com.docomodigital.exerciseapi.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/v1/**"))
                .apis(RequestHandlerSelectors.basePackage("com.docomodigital.exerciseapi.controllers"))
                .build()
                .apiInfo(apiDetails());
    }
    
    private ApiInfo apiDetails() {
        return new ApiInfo(
          "Purchase and Refund Api",
          "An application to manage purchases and refunds for customers.",
          "0.0.1",
          "This is free Licence version",
          new springfox.documentation.service.Contact("Amanuel Getachew", "https://github.com/amenski/exercise", "amantwd@gmail.com"),
          "API License",
          "https://github.com/amenski/exercise",
          Collections.emptyList());
    }
}