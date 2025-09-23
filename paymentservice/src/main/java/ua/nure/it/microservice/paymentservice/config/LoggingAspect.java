package ua.nure.it.microservice.paymentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging method execution in Controller and Service layers.
 * This class uses separate advice methods for each layer to apply different
 * default logging levels (INFO for controllers, DEBUG for services).
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut expression that targets all public methods within a class
     * annotated with @RestController.
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        // Dynamically get the logger for the class being advised
        Logger targetLogger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());

        // Log controller entry at INFO level
        if (targetLogger.isInfoEnabled()) {
            targetLogger.info(">>>> {}: ({}) START, ", methodName, args);
        }

        try {
            Object result = joinPoint.proceed();

            // Log controller exit at INFO level
            if (targetLogger.isInfoEnabled()) {
                targetLogger.info("<<<< {}: ={} END", methodName, result);
            }
            return result;
        } catch (Throwable throwable) {
            // Log exceptions at ERROR level
            targetLogger.error("<<<< {}: EXCEPTION. Error: {}", methodName, throwable.getMessage());
            throw throwable;
        }
    }

    /**
     * Pointcut expression that targets all public methods within a class
     * annotated with @Service.
     */
    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServices(ProceedingJoinPoint joinPoint) throws Throwable {
        // Dynamically get the logger for the class being advised
        Logger targetLogger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Log service entry at DEBUG level
        if (targetLogger.isDebugEnabled()) {
            targetLogger.debug(">>>> SERVICE {}.{}: START", className, methodName);
        }

        try {
            Object result = joinPoint.proceed();

            // Log service exit at DEBUG level
            if (targetLogger.isDebugEnabled()) {
                targetLogger.debug("<<<< SERVICE {}.{}: END", className, methodName);
            }
            return result;
        } catch (Throwable throwable) {
            // Log exceptions at ERROR level
            targetLogger.error("<<<< SERVICE {}.{}: EXCEPTION. Error: {}", className, methodName, throwable.getMessage());
            throw throwable;
        }
    }
}