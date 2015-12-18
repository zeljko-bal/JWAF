package org.jwaf.base.tools;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.FIELD)
public @interface Persistent
{
	Class<? extends Serializable> type() default Serializable.class;
}
