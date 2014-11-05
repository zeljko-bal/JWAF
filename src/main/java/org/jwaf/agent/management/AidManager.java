package org.jwaf.agent.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AidRepository;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Stateless
@LocalBean
public class AidManager
{
	@Inject
	private AidRepository aidRepository;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Produces @LocalPlatformAid
	public AgentIdentifier getPlatformAid()
	{
		return aidRepository.findAid(localPlatformName);
	}
}
