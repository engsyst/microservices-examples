java -jar .\openapi-generator-cli-7.9.0.jar generate ^
  -i http://localhost:8082/v3/api-docs ^
  --api-package ua.nure.it.microservice.orderservice.client.api ^
  --model-package ua.nure.it.microservice.orderservice.client.model ^
  --invoker-package ua.nure.it.microservice.orderservice.client.invoker ^
  --group-id ua.nure.it.microservice.orderservice ^
  --artifact-id spring-openapi-generator-api-client ^
  --artifact-version 0.0.1-SNAPSHOT ^
  -g java ^
  -c generator-config.json ^
  -o spring-openapi-generator-api-client


