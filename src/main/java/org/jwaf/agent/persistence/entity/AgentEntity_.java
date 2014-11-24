package org.jwaf.agent.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jwaf.message.persistence.entity.ACLMessage;

@Generated(value="Dali", date="2014-11-23T18:29:53.157+0100")
@StaticMetamodel(AgentEntity.class)
public class AgentEntity_ {
	public static volatile SingularAttribute<AgentEntity, AgentIdentifier> aid;
	public static volatile SingularAttribute<AgentEntity, AgentType> type;
	public static volatile MapAttribute<AgentEntity, String, String> privateData;
	public static volatile MapAttribute<AgentEntity, String, String> publicData;
	public static volatile ListAttribute<AgentEntity, ACLMessage> messages;
	public static volatile SingularAttribute<AgentEntity, String> state;
	public static volatile SingularAttribute<AgentEntity, Boolean> hasNewMessages;
}
