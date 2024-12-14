package com.frank.learnreactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.frank.learnreactive")
public class LearnreactiveApplication {
    private static final Logger logger = LoggerFactory.getLogger(LearnreactiveApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(LearnreactiveApplication.class);
            app.setRegisterShutdownHook(true);
            ConfigurableApplicationContext context = app.run(args);
            
            logger.info("Application started successfully");
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Application shutdown initiated");
                context.close();
            }));
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }
}