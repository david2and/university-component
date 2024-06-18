package com.javeriana.component.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("rest-config")
public class UrlConfig {
    String moodleUrl;
    String universityUrl;
}
