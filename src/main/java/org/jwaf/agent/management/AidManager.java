package org.jwaf.agent.management;

import java.util.List;
import java.util.Map;

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
		return aidRepository.find(localPlatformName);
	}
	
	public List<AgentIdentifier> find(Map<String, String> publicData)
	{
		return aidRepository.find(publicData);
	}
	
	public AgentIdentifier find(String name)
	{
		return aidRepository.find(name);
	}
	
	public AgentIdentifier manageAID(AgentIdentifier aid)
	{
		return aidRepository.manageAID(aid);
	}
	
	public AgentIdentifier createAid(AgentIdentifier aid)
	{
		return aidRepository.manageAID(aid);
	}
}
