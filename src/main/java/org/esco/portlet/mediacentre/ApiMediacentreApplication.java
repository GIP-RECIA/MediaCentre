package org.esco.portlet.mediacentre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = {"classpath:WEB-INF/*.xml"})
public class ApiMediacentreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiMediacentreApplication.class, args);
    }
}
