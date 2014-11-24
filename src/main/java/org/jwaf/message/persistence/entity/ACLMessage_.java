package org.jwaf.message.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Generated(value="Dali", date="2014-11-23T18:29:53.469+0100")
@StaticMetamodel(ACLMessage.class)
public class ACLMessage_ {
	public static volatile SingularAttribute<ACLMessage, Integer> id;
	public static volatile SingularAttribute<ACLMessage, String> performative;
	public static volatile SingularAttribute<ACLMessage, AgentIdentifier> sender;
	public static volatile ListAttribute<ACLMessage, AgentIdentifier> receiver;
	public static volatile SingularAttribute<ACLMessage, String> content;
	public static volatile SingularAttribute<ACLMessage, String> reply_with;
	public static volatile SingularAttribute<ACLMessage, Date> reply_by;
	public static volatile SingularAttribute<ACLMessage, String> reply_to;
	public static volatile ListAttribute<ACLMessage, AgentIdentifier> in_reply_to;
	public static volatile SingularAttribute<ACLMessage, String> language;
	public static volatile SingularAttribute<ACLMessage, String> encoding;
	public static volatile SingularAttribute<ACLMessage, String> ontology;
	public static volatile SingularAttribute<ACLMessage, String> protocol;
	public static volatile SingularAttribute<ACLMessage, String> conversation_id;
	public static volatile MapAttribute<ACLMessage, String, String> user_defined_parameters;
	public static volatile SingularAttribute<ACLMessage, Integer> unreadCount;
}
