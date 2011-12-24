package se.microcode.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Argument
{
    String name();
    ArgumentSource source() default ArgumentSource.PARAMETERS;
}
