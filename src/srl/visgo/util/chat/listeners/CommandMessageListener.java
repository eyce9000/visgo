package srl.visgo.util.chat.listeners;

import java.util.EventListener;

import org.jivesoftware.smack.Chat;

/***
 * Listener to track the commands/ notification being passed around
 * @author manoj
 *
 */
public interface CommandMessageListener extends EventListener{

	public void CommandReceived(Chat conversation, CommandMessage notification);
}
