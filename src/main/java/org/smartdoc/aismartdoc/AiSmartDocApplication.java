package org.smartdoc.aismartdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AiSmartDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiSmartDocApplication.class, args);
    }

}
