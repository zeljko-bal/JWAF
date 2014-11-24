package org.jwaf.remote.persistence.entity;

import java.net.URL;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Generated(value="Dali", date="2014-11-23T18:29:53.484+0100")
@StaticMetamodel(AgentPlatform.class)
public class AgentPlatform_ {
	public static volatile SingularAttribute<AgentPlatform, String> name;
	public static volatile ListAttribute<AgentPlatform, AgentIdentifier> agentIds;
	public static volatile SingularAttribute<AgentPlatform, URL> address;
}
