package org.jwaf.base.implementations.behaviour.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jwaf.base.implementations.behaviour.BehaviourTools;

/**
 * A class annotation to specify the name of the initial agent behaviour.
 * @see BehaviourTools
 * 
 * @author zeljko.bal
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
public @interface InitialBehaviour
{
	String value();
}
