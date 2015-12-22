package org.jwaf.agent.annotations.events;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Fired when an agent is fully initialized, 
 * {@link org.jwaf.agent.Agent#_setup _setup} method has finished successfully 
 * and the agent is in a {@link org.jwaf.agent.AgentState#PASSIVE PASSIVE} state.
 * 
 * @author zeljko.bal
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface AgentInitializedEvent
{}
