package com.samuraiiway.springbootgrafana.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTimed {

    String value() default "";

}
