package srl.visgo.util.chat.listeners;

import java.util.EventListener;

import org.jivesoftware.smack.Chat;

/***
 * Listener interface for tracking messages for the VISGO group
 * @author manoj
 *
 */
public interface GroupMessageListener extends EventListener {

	public void IncomingGroupMessage(GroupMessage currentMessage);
}
