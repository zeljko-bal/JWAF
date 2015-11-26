package org.jwaf.common;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jwaf.common.annotations.logger.NamedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class LoggerProducer
{
	@Produces @NamedLogger
	public Logger getLogger(InjectionPoint ip)
	{
		String name = ip.getAnnotated().getAnnotation(NamedLogger.class).value();
		return LoggerFactory.getLogger(name);
	}
	
	@Produces @Default
	public Logger getDefaultLogger(InjectionPoint ip)
	{
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getSimpleName());
	}
}
