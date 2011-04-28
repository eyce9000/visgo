package srl.visgo.util.chat.listeners;

import java.util.EventListener;

import org.jivesoftware.smack.packet.Presence;

/***
 * Interface for the listener which will notify the status of the users 
 * working with the visgo
 * @author manoj
 *
 */
public interface StatusChangeListener extends EventListener{

	/**
	 * Status change event
	 * @param userID - email id to which the status belongs to
	 * @param status - Status is the one of the following - Available, Unavailable and so on 
	 * 				- it is one of the values of Presence.Type constants.
	 */
	public void StatusChanged(String userID, Presence status);
}
