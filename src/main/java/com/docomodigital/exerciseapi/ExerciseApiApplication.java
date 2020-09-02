package com.docomodigital.exerciseapi;

import java.net.URL;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ExerciseApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExerciseApiApplication.class);

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
        reloadLogger();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static void reloadLogger() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            ContextInitializer ci = new ContextInitializer(loggerContext);
            URL url = ci.findURLOfDefaultConfigurationFile(true);
            loggerContext.reset();
            ci.configureByResource(url);
        } catch (Exception e) {
            logger.error("{} {}", ExerciseApiApplication.class.getSimpleName(), e);
        }
    }
}
