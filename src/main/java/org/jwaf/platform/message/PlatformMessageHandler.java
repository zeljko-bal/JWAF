package org.jwaf.platform.message;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface PlatformMessageHandler
{
	void handlePlatformMessage(ACLMessage content);
}
