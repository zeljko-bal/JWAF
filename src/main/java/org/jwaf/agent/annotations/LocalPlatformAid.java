package org.jwaf.agent.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

/**
 * A qualifier for injecting the {@link AgentIdentifier} that represents the local platform.
 * 
 * @author zeljko.bal
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface LocalPlatformAid
{}
