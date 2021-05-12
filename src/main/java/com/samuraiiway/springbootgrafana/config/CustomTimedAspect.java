package com.samuraiiway.springbootgrafana.config;

import com.samuraiiway.springbootgrafana.annotation.CustomTimed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

@Slf4j
@Aspect
public class CustomTimedAspect {

    private final MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tags;

    public CustomTimedAspect(MeterRegistry registry) {
        this(registry, (pjp) -> Tags.of(new String[]{"class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method", pjp.getStaticPart().getSignature().getName()}));
    }

    public CustomTimedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tags) {
        this.registry = registry;
        this.tags = tags;
    }

    @Around("@annotation(com.samuraiiway.springbootgrafana.annotation.CustomTimed)")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        CustomTimed customTimed = method.getAnnotation(CustomTimed.class);

        String metricName = customTimed.value();
        String[] tags = buildParameterTags(method.getParameters(), pjp.getArgs());
        Object result = null;

        Timer.Sample timer = Timer.start(this.registry);

        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable t) {
            result = t.getClass().getSimpleName();
            throw t;
        } finally {
            timer.stop(Timer
                    .builder(metricName)
                    .tags(this.tags.apply(pjp))
                    .tags(tags)
                    .tags("result", String.valueOf(result))
                    .register(registry));
        }
    }

    private String[] buildParameterTags(Parameter[] params, Object[] args) {
        if (params.length != args.length) {
            return new String[]{};
        }

        String[] tags = new String[params.length + args.length];

        for (int i = 0, j = 0; i < params.length; i++, j += 2) {
            tags[j] = params[i].getName();
            tags[j + 1] = String.valueOf(args[i]);
        }

        return tags;
    }

}
