package org.jwaf.agent.annotations.events;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jwaf.agent.persistence.entity.AgentEntity;

/**
 * Fired whenever an {@link AgentEntity} is removed from the platform.
 * 
 * @author zeljko.bal
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface AgentRemovedEvent
{}
