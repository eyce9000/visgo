package srl.visgo.util.chat;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.CommandMessageListener;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.GroupMessageListener;
import srl.visgo.util.chat.listeners.IndividualMessage;
import srl.visgo.util.chat.listeners.IndividualMessageListener;

import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;	


public class MessageProcessor implements PacketListener{

	private static final String XMLVERSION = "<?xml version=\"1.0\"?>";
	private EventListenerList commandMessageListeners = null; // listeners for command / notification
	private EventListenerList groupMessageListeners = null; // listeners for the group message 
	private EventListenerList individualMessageListeners = null; // listeners for the individual message
	
	public MessageProcessor(){
		
		commandMessageListeners = new EventListenerList();
		groupMessageListeners = new EventListenerList();
		individualMessageListeners = new EventListenerList();
	}
	
	/**
	 * Add your listener to the list and start receiving commands/notifications 
	 * @param commandListener
	 */
	public void addCommandMessageListener(CommandMessageListener commandListener){
		
		commandMessageListeners.add(CommandMessageListener.class, commandListener);
	}
	
	/**
	 * Add your GroupMessageListener to the list and start listening to the messages sent to the group
	 * useful to direct group messages to corresponding window
	 * @param gMessageListener
	 */
	public void addGroupMessageListener(GroupMessageListener gMessageListener){
		
		groupMessageListeners.add(GroupMessageListener.class, gMessageListener);
	}

	/**
	 * Add your Individual message listener to the list and listen to individual conversation
	 * @param iMessageListener
	 */
	public void addIndividualMessageListener(IndividualMessageListener iMessageListener){
		
		individualMessageListeners.add(IndividualMessageListener.class, iMessageListener);
	}
	/**
	 * Remove your listener when you do not need the notifications
	 * @param commandListener
	 */
	public void removeCommandMessageListener(CommandMessageListener commandListener){
		
		commandMessageListeners.remove(CommandMessageListener.class, commandListener);
	}
	
	/**
	 * delist your listener and stop listening to group messages
	 * @param gMessageListener
	 */
	public void removeGroupMessageListener(GroupMessageListener gMessageListener){
		
		commandMessageListeners.remove(GroupMessageListener.class, gMessageListener);
	}

	/**
	 * delist your self and stop listening to individual conversation
	 * @param IMessageListener
	 */
	public void removeIndividualMessageListener(IndividualMessageListener IMessageListener){
		
		commandMessageListeners.remove(IndividualMessageListener.class, IMessageListener);
	}
	
	
	/***
	 * function that triggers the notification event
	 * This one passes the event to all the command listeners
	 * @param conversation - the chat conversation it belongs to. 
	 * @param notification - the command itself
	 */
	private void fireCommandReceived(CommandMessage notification){
		Object[] listenerList = commandMessageListeners.getListenerList();
		
		for(int i = 0; i < listenerList.length - 1; i++){
			
			((CommandMessageListener)listenerList[i+1]).CommandReceived(notification);
		}
	}
	
	/***
	 * function that triggers the group message event
	 * This one passes the event to all the group message listeners
	 * @param conversation - the chat conversation it belongs to. 
	 * @param gMessage - the message itself
	 */
	private void fireGroupMessageReceived(GroupMessage gMessage){
		Object[] listenerList = groupMessageListeners.getListenerList();
		
		for(int i = 0; i < listenerList.length - 1; i++){
			
			((GroupMessageListener)listenerList[i + 1]).IncomingGroupMessage(gMessage);
		}
	}
	
	/***
	 * function that triggers the group message event
	 * This one passes the event to all the group message listeners
	 * @param conversation - the chat conversation it belongs to. 
	 * @param gMessage - the message itself
	 */
	private void fireIndividualMessageReceived(IndividualMessage iMessage){
		Object[] listenerList = individualMessageListeners.getListenerList();
		
		for(int i = 0; i < listenerList.length - 1; i++){
			
			((IndividualMessageListener)listenerList[i + 1]).IncomingIndividualMessage(iMessage);
		}
	}
	
	
	@Override
	public void processPacket(Packet currentMessage) {
		
		// Creating the DOM parser 
		DocumentBuilder builder;
		
		Document parsedXMLMessage = null;
		String xmlMessage = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				
			xmlMessage = XMLVERSION + ((Message)currentMessage).getBody();
			
			// creating the XML document object
			parsedXMLMessage = builder.parse(new ByteArrayInputStream(xmlMessage.getBytes("UTF-8")));
			
			
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		if(parsedXMLMessage != null){
			
			NodeList commands = parsedXMLMessage.getElementsByTagName(CommandMessage.TAGNAME);
			NodeList gMessages = parsedXMLMessage.getElementsByTagName(GroupMessage.TAGNAME);
			NodeList iMessages = parsedXMLMessage.getElementsByTagName(IndividualMessage.TAGNAME);
			
			// parse out the command message and fire notification to listeners
			if(commands.getLength() > 0){
				
				Element command = (Element)commands.item(0);
				CommandMessage notification= CommandMessage.parse((Message)currentMessage, command);
				fireCommandReceived(notification);
			}
			// parse out the group message and fire notification to listeners
			if(gMessages.getLength() > 0){
				
				Element gMessage = (Element)gMessages.item(0);
				GroupMessage notification= GroupMessage.parse((Message)currentMessage, gMessage);
				fireGroupMessageReceived(notification);
			}
			// parse out the individual message and fire notification to listeners
			if(iMessages.getLength() > 0){
				
				Element iMessage = (Element)iMessages.item(0);
				IndividualMessage notification= IndividualMessage.parse((Message)currentMessage, iMessage);
				fireIndividualMessageReceived(notification);
			}
		}
	}

	
	
	

}
