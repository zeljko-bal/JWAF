package org.jwaf.platform;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface PlatformMessageHandler
{
	void handlePlatformMessage(ACLMessage content);
}
