package ai.esp.com.aop;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CtrlTimerAdvice
 *
 * @author pqdongo
 * @since 2025/02/14
 */

@Slf4j
@Aspect
@Component
public class CtrlTimerAdvice {

    @Around("@annotation(requestMapping)")
    public Object requestMappingAdvice(ProceedingJoinPoint thisJoinPoint, RequestMapping requestMapping) throws Throwable {
        String path = Arrays.toString(requestMapping.value());
        return process(thisJoinPoint, path);
    }

    @Around("@annotation(getMapping)")
    public Object getMappingAdvice(ProceedingJoinPoint thisJoinPoint, GetMapping getMapping) throws Throwable {
        String path = Arrays.toString(getMapping.value());
        return process(thisJoinPoint, path);
    }

    @Around("@annotation(postMapping)")
    public Object getMappingAdvice(ProceedingJoinPoint thisJoinPoint, PostMapping postMapping) throws Throwable {
        String path = Arrays.toString(postMapping.value());
        return process(thisJoinPoint, path);
    }

    @Around("@annotation(putMapping)")
    public Object getMappingAdvice(ProceedingJoinPoint thisJoinPoint, PutMapping putMapping) throws Throwable {
        String path = Arrays.toString(putMapping.value());
        return process(thisJoinPoint, path);
    }

    private Object process(ProceedingJoinPoint pjp, String path) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        long endTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String logName = methodSignature.getMethod().getName() + "(" + path + ")";
        Object[] args = pjp.getArgs();
        if (args.length > 0 && args[0] instanceof HttpServletRequestWrapper) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }
        log.info("responseTime:{} url:{} logName:{} params:{}", (endTime - startTime), path, logName, args);
        return result;
    }
}
