package ua.nure.it.microservice.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class JsonAgainstSchemaValidator {

    final ObjectMapper mapper = new ObjectMapper();

    public void validate(String schemaFileName, String... jsonFileNames) {
        JsonSchema schema = null;
        String jsonFile = null;
        try {
            schema = getJsonSchema("/" +schemaFileName);

            // Try to validate a valid JSON instance
            for (String jsonFileName : jsonFileNames) {
                jsonFile = jsonFileName;
                validate(schema, jsonFileName);
            }
        } catch (IOException | ProcessingException e) {
            log.error("Error processing file '{}' with the cause '{}'", jsonFile, e.getMessage());
        }
    }

    private void validate(JsonSchema schema, String jsonFileName) throws IOException, ProcessingException {
        System.out.printf("Validating JSON... '%s'%n", jsonFileName);
        try (InputStream validJsonStream = JsonAgainstSchemaValidator.class
                .getResourceAsStream("/" + jsonFileName)) {
            JsonNode jsonNode = mapper.readTree(validJsonStream);
            ProcessingReport report = schema.validate(jsonNode);
            if (!report.isSuccess()) {
//                System.out.printf("JSON '%s' is not valid.%n", jsonFileName);
                printReport(report);
                // Here, you would proceed with:
                // Order order = mapper.treeToValue(jsonNode, Order.class);
                return;
            }
            System.out.printf("JSON file '%s' is valid. Now you can safely map to an object.%n%n", jsonFileName);
        }

    }

    private JsonSchema getJsonSchema(String schemaFileName) throws ProcessingException, IOException {
//        JsonOrderValidationExample.class.getResource(schemaFileName)
        try (InputStream schemaStream = JsonAgainstSchemaValidator.class
                .getResourceAsStream(schemaFileName)) {
            JsonNode schemaNode = mapper.readTree(schemaStream);
            if (schemaNode == null) {
                throw new ProcessingException("SchemaNode is null");
            }

            // Get the schema factory
            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            return factory.getJsonSchema(schemaNode);
        }
    }

    private void printReport(ProcessingReport report) {
        if (report.isSuccess()) {
            System.out.println("Validation successful.");
        } else {
            System.out.println("Validation failed.");
            report.forEach(message -> System.out.println("  - " + message));
        }
        System.out.println();
    }
}
