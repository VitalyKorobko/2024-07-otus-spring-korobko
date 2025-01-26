package ru.otus.hw.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(ru.otus.hw.logging.LogCustom)")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        logBeforeMethod(joinPoint);
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (EntityNotFoundException e) {
            LOG.error("{} call method {} and can't find book with args {}",
                    joinPoint.getThis(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs()
            );
            log(e);
        } catch (RuntimeException e) {
            log(e);
        }
        logAfterMethod(joinPoint, result);

        return result;
    }

    private void logAfterMethod(ProceedingJoinPoint joinPoint, Object result) {
        LOG.info("method {} of object {} return value {}",
                joinPoint.getSignature().getName(),
                joinPoint.getThis(),
                result
        );
    }


    private void logBeforeMethod(JoinPoint joinPoint) {
        LOG.info("Object {} call method {} with args {}",
                joinPoint.getThis(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    private String getStackTrace(Exception e) {
        var stringBuilder = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            stringBuilder.append(el.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void log(Exception e) {
        LOG.error("{}: {}{}{}", e.getClass(), e.getMessage(), "\n", getStackTrace(e));
    }

}