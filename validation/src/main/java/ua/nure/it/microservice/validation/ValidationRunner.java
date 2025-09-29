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

        // JSON
        jsonValidator.validate("json/restaurant-schema.json",
                "json/restaurant.json", "json/restaurant-invalid.json");
        jsonValidator.validate("json/order-short-schema.json",
                "json/order-short-valid.json", "json/order-short-invalid.json");
        jsonValidator.validate("json/menu-item-schema.json",
                "json/menu-item.json", "json/menu-item-invalid.json");

        // XML
        xmlValidator.validate("xml/order-schema.xsd",
                "xml/order-short-valid.xml", "xml/order-short-invalid.xml");
        xmlValidator.validate("xml/xml-v1-1/order-schema.xsd",
                "xml/xml-v1-1/validOrder.xml", "xml/xml-v1-1/invalidOrder.xml");
    }
}