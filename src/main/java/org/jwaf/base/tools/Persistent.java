package org.jwaf.base.tools;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Agent fields annotated with this annotation can be automaticly persisted using the {@link AutoPersister} tool.
 * @see AutoPersister
 * 
 * @author zeljko.bal
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.FIELD)
public @interface Persistent
{
	Class<? extends Serializable> type() default Serializable.class;
}
