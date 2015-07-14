package org.jwaf.common.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TypeAttributes.class)
public @interface TypeAttribute
{
	String key();
	String value();
}
