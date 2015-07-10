package org.jwaf.agent.implementations.fsm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface StateCallback
{
	String state();
	boolean initial() default false;
}
