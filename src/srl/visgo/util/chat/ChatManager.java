package srl.visgo.util.chat;

import java.util.HashMap;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.filter.*;

import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.IndividualMessage;

public class ChatManager implements ChatManagerListener {

	private static String SERVERNAME = "gmail.com"; // Name of the server to connect
	
	private String loginName; /// login user name for the gtalk server
	
	private String password; /// password for the user account.
	
	private XMPPConnection serverConnection = null;
	
	private Roster friendsList = null;
	
	private HashMap<String, Chat> chatInstanceMap = null; /// Mapping the instance of the chat with the corresponding jabber id object.
	
	private MessageProcessor messageInterpreter = null;
	
	public XMPPConnection getServerConnection() {
		return serverConnection;
	}

	public Roster getFriendsList() {
		return friendsList;
	}

	public MessageProcessor getMessageInterpreter() {
		return messageInterpreter;
	}
	
	/***
	 * Constructor for the Chat Manager
	 * @param loginName - user name for the IM account
	 * @param password - password for the IM account
	 */
	public ChatManager(String loginName, String password){
	
		this.loginName = loginName;
		
		this.password = password;
		
		messageInterpreter = new MessageProcessor();
		
		chatInstanceMap = new HashMap<String, Chat>();
	}
	
	/***
	 * Creates a connection to the server for every chat window in VISGO.
	 */
	public void connect() throws XMPPException, IllegalStateException{
		
		ConnectionConfiguration cc = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		
		serverConnection = new XMPPConnection(cc);
		
		serverConnection.connect();
				
		serverConnection.login(this.loginName, this.password, "VISGO");
		
		serverConnection.addPacketListener(messageInterpreter, new PacketTypeFilter(Message.class));
	}
	
	/**
	 * Function to return the list of friends in IM.
	 * @return
	 */
	public Roster getListofFriends(){
		
		if(friendsList == null && serverConnection.isConnected())
			
			friendsList = serverConnection.getRoster();
		else
			
			friendsList = null;
		
		return friendsList;
	}
	
	/***
	 * Creates a Chat instance to track the chat messages
	 * @param friendName - name of the friend to IM with
	 * @param conversationID - Unique ID of the Chat instance.
	 * This can be used to differentiate the commands from the chat conversation
	 * 
	 * @param conversationListener - Listens to the received messages.
	 */
	public boolean createChat(String friendName, String conversationID, MessageListener conversationListener){
		
		boolean chatCreated = false;
		
		if(serverConnection.isConnected()){
			
			Chat newConversation = serverConnection.getChatManager().createChat(friendName, conversationID, conversationListener);
						
			chatInstanceMap.put(friendName, newConversation);
			
			chatCreated = true;
		}	
		
		return chatCreated;
	}
	
	/***
	 * Function to peek into conversation.
	 * @param conversationID - the conversation ID. is unique
	 * @param conversationListener - listener object to peek into conversation
	 */
	public void addConversationListener(String conversationID, MessageListener conversationListener){
		
		chatInstanceMap.get(conversationID).addMessageListener(conversationListener);
	}
	
	/**
	 * Function to check if a conversation exists.
	 * @param userID - email ID of the user 
	 * @return
	 */
	public boolean doesUserIDExist(String userID){
		
		return chatInstanceMap.containsKey(userID);
	}
	
	/**
	 * Function to remove the listener object on the chat
	 * @param userID
	 */
	public void removeConversationListener(String userID){
		
		if(chatInstanceMap.containsKey(userID))
			chatInstanceMap.remove(userID);
	}
	
	/***
	 * Function to send message to individual user IDs
	 * @param userID - user ID 
	 * @param message - message in string format
	 */
	private void sendMessage(String userID, String message){
		
		if(chatInstanceMap.containsKey(userID)){
			
			try {
				chatInstanceMap.get(userID).sendMessage(message);
				
			} catch (XMPPException e) {
				
				System.out.print("Problem with sending message :: " + message);
				//e.printStackTrace();
			}
		}
	}
	
	/***
	 * Function to send a message to the whole group.
	 * @param message
	 */
	private void sendGroupMessage(String message){
		
		Object[] userIDs = chatInstanceMap.keySet().toArray();
		
		for(int i = 0; i < userIDs.length; i++ ){
			
			sendMessage((String) userIDs[i], message);
		}
	}
	
	/**
	 * Method to disconnect from the IM Server.
	 */
	public void disconnect(){
		
		serverConnection.disconnect();
	}

	@Override
	public void chatCreated(Chat conversation, boolean localCreated) {

		// chat was not created locally
		if(!localCreated){
			
			chatInstanceMap.put(conversation.getThreadID(), conversation);
					
		}
	}

	/***
	 * use this to continue individual conversations
	 * @param userID - user Id of the person to send the message
	 * @param iMessage - the message object itself
	 */
	public void sendIndividualMessage(String userID, IndividualMessage iMessage){
		
		MessageCreator mc = new MessageCreator(iMessage	);
		
		sendMessage(userID, mc.toXML());
		
	}

	/***
	 * use this to send notification/ commands to individuals
	 * @param userID
	 * @param command
	 */
	public void sendIndividualCommand(String userID, CommandMessage command){
		
		MessageCreator mc = new MessageCreator(command);
		
		sendMessage(userID, mc.toXML());
		
	}
	
	/***
	 * use this to send notificaitons to the whole group
	 * @param command
	 */
	public void sendGroupCommand(CommandMessage command){
		
		MessageCreator mc = new MessageCreator(command);
		
		sendGroupMessage(mc.toXML());
		
	}
	
	/***
	 * use this to send group messages 
	 * @param gMessage
	 */
	public GroupMessage sendGroupMessage(GroupMessage gMessage){
		
		MessageCreator mc = new MessageCreator(gMessage);
		
		sendGroupMessage(mc.toXML());
		
		return mc.getGMessage();
		
	}
}
