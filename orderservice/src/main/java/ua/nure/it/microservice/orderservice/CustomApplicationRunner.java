package ua.nure.it.microservice.orderservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class CustomApplicationRunner implements ApplicationRunner {

    private final Environment environment;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    public CustomApplicationRunner(Environment environment, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.environment = environment;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(environment.getProperty("spring.application.name", "Restaurant Service"));
        String baseUrl = "http://localhost:" + environment.getProperty("server.port", "8080");
        System.out.println(baseUrl);
        System.out.println("Registered endpoints:");
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = new TreeMap<>(
                (RequestMappingInfo info1, RequestMappingInfo info2) -> {
                    RequestCondition<Object> pathCondition1 = info1.getActivePatternsCondition();
                    RequestCondition<Object> pathCondition2 = info2.getActivePatternsCondition();
                        return pathCondition1.toString()
                                .compareTo(pathCondition2.toString());
                });
        handlerMethods.putAll(requestMappingHandlerMapping.getHandlerMethods());

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod method = entry.getValue();
            System.out.println("  " + requestMappingInfoToString(mappingInfo, baseUrl) + " --> " + method.getMethod().getDeclaringClass().getSimpleName()
                    + "#" + method.getMethod().getName());
        }
    }

    public String requestMappingInfoToString(RequestMappingInfo info, String url) {
        StringBuilder builder = new StringBuilder("{");
        if (!info.getMethodsCondition().isEmpty()) {
            Set<RequestMethod> httpMethods = info.getMethodsCondition().getMethods();
            builder.append(httpMethods.size() == 1 ? httpMethods.iterator().next() : httpMethods);
        }

        builder.append(" [").append(url).append(info.getActivePatternsCondition()
                .toString().replace("[", ""));
        if (!info.getParamsCondition().isEmpty()) {
            builder.append(", params ").append(info.getParamsCondition());
        }

        if (!info.getHeadersCondition().isEmpty()) {
            builder.append(", headers ").append(info.getHeadersCondition());
        }

        if (!info.getConsumesCondition().isEmpty()) {
            builder.append(", consumes ").append(info.getConsumesCondition());
        }

        if (!info.getProducesCondition().isEmpty()) {
            builder.append(", produces ").append(info.getProducesCondition());
        }

        builder.append('}');
        return builder.toString();
    }
}