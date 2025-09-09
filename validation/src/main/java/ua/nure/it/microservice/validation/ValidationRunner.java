package ua.nure.it.microservice.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ValidationRunner implements ApplicationRunner {

    final Environment environment;
    final JsonAgainstSchemaValidator jsonValidator;
    final XmlAgainstSchemaValidator xmlValidator;

    @Autowired
    public ValidationRunner(Environment environment, JsonAgainstSchemaValidator jsonValidator, XmlAgainstSchemaValidator xmlValidator) {
        this.environment = environment;
        this.jsonValidator = jsonValidator;
        this.xmlValidator = xmlValidator;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(environment.getProperty("spring.application.name", "Validation Application"));
        jsonValidator.validate("order-schema.json",
                "validOrder.json", "invalidOrder.json");
        xmlValidator.validate("order-schema.xsd",
                "validOrder.xml", "invalidOrder.xml");
    }
}