package org.jwaf.message.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-23T18:29:53.484+0100")
@StaticMetamodel(OutboxEntry.class)
public class OutboxEntry_ {
	public static volatile SingularAttribute<OutboxEntry, String> receiverName;
	public static volatile SingularAttribute<OutboxEntry, ACLMessage> message;
}
