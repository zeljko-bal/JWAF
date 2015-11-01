package org.jwaf.data.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.data.persistence.entity.AgentDataType;
import org.jwaf.data.persistence.repository.AgentDataRepository;
import org.jwaf.data.persistence.repository.DataStore;

@Stateless
@LocalBean
public class AgentDataManager
{
	@Inject
	private AgentDataRepository dataRepo;
	
	public DataStore getDataStore(String agentName, AgentDataType dataType)
	{
		return new DataStore(dataRepo, dataType, agentName);
	}
}
