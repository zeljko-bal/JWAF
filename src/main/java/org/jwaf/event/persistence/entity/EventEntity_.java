package org.jwaf.event.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Generated(value="Dali", date="2014-11-23T18:29:53.469+0100")
@StaticMetamodel(EventEntity.class)
public class EventEntity_ {
	public static volatile SingularAttribute<EventEntity, String> name;
	public static volatile SingularAttribute<EventEntity, String> type;
	public static volatile ListAttribute<EventEntity, AgentIdentifier> registeredAgents;
}
