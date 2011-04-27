package srl.visgo.util.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.filter.*;

import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.IndividualMessage;
import srl.visgo.util.chat.listeners.StatusChangeListener;

public class ChatManager implements ChatManagerListener, RosterListener {

	private static String SERVERNAME = "gmail.com"; // Name of the server to connect

	public static String CLIENT_ID = "Visgo";

	private String loginName; /// login user name for the gtalk server

	private String password; /// password for the user account.

	private XMPPConnection serverConnection = null;

	private Roster friendsList = null;

	private HashMap<String, Chat> chatInstanceMap = null; /// Mapping the instance of the chat with the corresponding jabber id object.

	private MessageProcessor messageInterpreter = null;

	private EventListenerList statusChangeListeners = null;

	public XMPPConnection getServerConnection() {
		return serverConnection;
	}

	public Roster getFriendsList() {
		return friendsList;
	}

	public MessageProcessor getMessageInterpreter() {
		return messageInterpreter;
	}
	/**
	 * Function to return the name of the friend corresponding to the gmail id
	 * @param userID - gmail id of your friend
	 * @return -  name of your friend
	 */
	public String getNameoftheFriend(String userID){

		if(friendsList.getEntry(userID)!=null){

			return friendsList.getEntry(userID).getName();
		}
		return null;
	}



	public boolean isLoggedInWithVisgo(String username){
		Iterator<Presence> presences = friendsList.getPresences(username);
		while(presences.hasNext()){
			Presence presence = presences.next();
			if(usingVisgo(presence) & presence.getType() == Presence.Type.available){
				return true;
			}
		}

		return false;
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

		serverConnection.login(this.loginName, this.password, CLIENT_ID+".");

		friendsList = serverConnection.getRoster();

		friendsList.addRosterListener(this);

		statusChangeListeners = new EventListenerList();

		serverConnection.addPacketListener(messageInterpreter, new PacketTypeFilter(Message.class));
	}

	/**
	 * Function to return the list of friends in IM.
	 * @return
	 */
	public Roster getListofFriends(){

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

			if(!chatInstanceMap.containsKey(friendName))
			{
				Chat newConversation = serverConnection.getChatManager().createChat(friendName, conversationID, conversationListener);

				chatInstanceMap.put(friendName, newConversation);

				chatCreated = true;
			}
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
	public void sendMessage(String userID, String message){

		// Checking if the user is available before sending the message.


		if(chatInstanceMap.containsKey(userID)
				&& isLoggedInWithVisgo(userID)){

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

	@Override
	public void entriesAdded(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entriesDeleted(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entriesUpdated(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void presenceChanged(Presence arg0) {
		String[] slashsplit = arg0.getFrom().split("\\/");
		String  userID = slashsplit[0];
		String  clientID = extractClientID(arg0);


		System.out.println("Presence changed:"+userID+" "+arg0.getType());
		System.out.println("Entry User:"+friendsList.getEntry(userID).getUser());

		System.out.println("Client ID:"+clientID);

		if(usingVisgo(arg0)){
			this.fireStatusChangeEvent(userID, arg0.getType());
		}
	}

	/**
	 * listen to changes in the status of the users involved in the group
	 * @param listener
	 */
	public void addStatusChangeListener(StatusChangeListener listener){

		statusChangeListeners.add(StatusChangeListener.class, listener);
	}

	/** stop listening to changes in the status of the users in the group
	 * 
	 * @param listener
	 */
	public void removeStatusChangeListener(StatusChangeListener listener){

		statusChangeListeners.remove(StatusChangeListener.class, listener);
	}



	public void fireStatusChangeEvent(String userID, Presence.Type status){
		Object[] listenerList = statusChangeListeners.getListenerList();

		for (int i = listenerList.length-2; i>=0; i-=2) {
			if (listenerList[i]==StatusChangeListener.class) {
				((StatusChangeListener)listenerList[i+1]).StatusChanged(userID, status);
			}
		}
	}


	public static String extractClientID(Presence presence){
		String slashSplit[] = presence.getFrom().split("\\/");
		if(slashSplit.length > 1){
			return slashSplit[1].split("\\.")[0];
		}
		return "";
	}
	public static boolean usingVisgo(Presence presence){
		return extractClientID(presence).equals(CLIENT_ID);
	}
	
	/**
	 * Function to set the status of the visgo user
	 * 
	 * @param status - status message to be communicated across 
	 */
	public void setStatus(String status)
	{
		Presence statusPacket = new Presence(Presence.Type.available, status, 0, Presence.Mode.available);
		
		serverConnection.sendPacket(statusPacket);
	}
}
