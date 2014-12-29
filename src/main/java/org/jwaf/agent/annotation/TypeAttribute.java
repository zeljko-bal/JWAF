package org.jwaf.agent.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TypeAttribute
{
	String key();
	String value();
}
