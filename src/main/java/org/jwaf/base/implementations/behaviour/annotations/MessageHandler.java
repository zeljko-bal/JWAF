package org.jwaf.base.implementations.behaviour.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface MessageHandler
{
	String performative() default "";
	String behaviour()default "";
}
