package com.example.doglistener.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AudioProperties.class,
        DetectorProperties.class,
        ModelProperties.class,
        ResponseProperties.class
})
public class ApplicationConfiguration {
}
