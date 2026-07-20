package com.example.doglistener;

import com.example.doglistener.service.DetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application implements CommandLineRunner {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Application.class);

    private final DetectionService detectionService;

    @Autowired
    public Application(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) {

        LOGGER.info("=====================================");
        LOGGER.info(" Dog Bark Listener");
        LOGGER.info("=====================================");

        try {

            detectionService.start();

        }
        catch (Exception ex) {

            LOGGER.error("Fatal error", ex);

            System.exit(1);

        }

    }

}
