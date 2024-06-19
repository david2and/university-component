package com.javeriana.component.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("university-endpoints")
public class UniversityEndpointsConfig {
    String usersEndpoint;
    String coursesEndpoint;
    String registrationsEndpoint;
    String categoriesEndpoint;
    String syncGrades;

}
