package srl.visgo.util.chat.listeners;

import java.util.EventListener;

import org.jivesoftware.smack.Chat;

public interface IndividualMessageListener extends EventListener {
	
	public void IncomingIndividualMessage(IndividualMessage currentMessage);

}
