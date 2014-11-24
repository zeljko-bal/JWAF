package org.jwaf.agent.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-23T18:29:53.469+0100")
@StaticMetamodel(AgentIdentifier.class)
public class AgentIdentifier_ {
	public static volatile SingularAttribute<AgentIdentifier, String> name;
	public static volatile ListAttribute<AgentIdentifier, String> addresses;
	public static volatile ListAttribute<AgentIdentifier, AgentIdentifier> resolvers;
	public static volatile MapAttribute<AgentIdentifier, String, String> userDefinedParameters;
	public static volatile SingularAttribute<AgentIdentifier, Integer> refCount;
}
