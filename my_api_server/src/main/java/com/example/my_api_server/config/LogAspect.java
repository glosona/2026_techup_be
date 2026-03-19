package com.example.my_api_server.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Around("execution(* com.example.my_api_server.service..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        }
//        catch (Throwable e) {
//            throw e;
//        }
        finally {
            long endTime = System.currentTimeMillis();
            log.info(joinPoint.getSignature() + " 실행 시간: " + (endTime - startTime) + "ms");
        }
    }

}
