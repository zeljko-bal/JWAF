package org.jwaf.base.implementations.behaviour.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jwaf.base.implementations.behaviour.BehaviourTools;

/**
 * A method annotation declaring that the method is a message handler.
 * @see BehaviourTools
 * 
 * @author zeljko.bal
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface MessageHandler
{
	/**
	 * the performative of the messages that this method can handle
	 */
	String performative() default "";
	
	/**
	 * the behaviour in which the agent uses this handler
	 */
	String behaviour()default "";
}
