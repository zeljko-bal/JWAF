package org.jwaf.agent.management;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jwaf.agent.annotations.LocalPlatformAid;
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
		return aidRepository.find(localPlatformName);
	}
	
	public List<AgentIdentifier> find(Map<String, String> userDefinedParameters)
	{
		return aidRepository.find(userDefinedParameters);
	}
	
	public AgentIdentifier find(String name)
	{
		return aidRepository.find(name);
	}
	
	public AgentIdentifier manageAID(AgentIdentifier aid)
	{
		return aidRepository.manageAID(aid, false);
	}
	
	public AgentIdentifier createAid(AgentIdentifier aid)
	{
		return aidRepository.manageAID(aid, false);
	}
}
