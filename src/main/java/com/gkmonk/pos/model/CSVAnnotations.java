package com.gkmonk.pos.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Make annotation available at runtime
@Target(ElementType.FIELD) // Apply annotation to fields
public @interface CSVAnnotations {
    String value() default "default value";
    int number() default 0;
    String column() default "not mapped";
}
