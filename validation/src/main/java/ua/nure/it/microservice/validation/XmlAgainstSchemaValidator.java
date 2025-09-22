package ua.nure.it.microservice.validation;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@Slf4j
public class XmlAgainstSchemaValidator {
	
	@Value("${xml.schema.version:http://www.w3.org/2001/XMLSchema}")
	String xmlSchemaVersion;

    public void validate(String schemaFileName, String... xmlFileNames) {

        try {
            // obtain schema
            Schema schema = getSchema(schemaFileName);
            // creating validator
            Validator validator = schema.newValidator();
            // run validation for each file
            for (String xmlFileName : xmlFileNames) {
                validate(validator, xmlFileName);
            }

        } catch (Exception e) {
            log.error("Cannot create Schema for {} because {}\nAborted...", schemaFileName, e.getMessage());
//            throw new RuntimeException(e);
        }
    }

    private Schema getSchema(String schemaFileName) throws SAXException {
        // obtain schema factory
        SchemaFactory sf = SchemaFactory.newInstance(xmlSchemaVersion);
//        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");

        // obtain schema
        Schema schema = null;
        if ("".equals(schemaFileName)) {
            // setup validation against XSD pointed in XML
            schema = sf.newSchema();
        } else {
            // setup validation against external XSD
            URL url = XmlAgainstSchemaValidator.class.getResource("/" + schemaFileName);
            schema = sf.newSchema(url);
        }
        return schema;
    }

    private void validate(Schema schema, String xmlFileName) {
        Validator validator = schema.newValidator();
        validate(validator, xmlFileName);
    }

    private void validate(Validator validator, String xmlFileName) {
        try {
            InputStream resourceAsStream = XmlAgainstSchemaValidator.class.getResourceAsStream("/" + xmlFileName);
			Source source = new StreamSource(
            	 resourceAsStream);
            validator.validate(source);
            System.out.println(xmlFileName + " is valid.");
        } catch (IOException | SAXException ex) {
            log.error("{} is not valid because of {}", xmlFileName, ex.getMessage());
        }
    }
}
