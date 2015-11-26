package org.jwaf.common.annotations.attributes;

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
