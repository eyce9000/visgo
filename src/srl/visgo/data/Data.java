package srl.visgo.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import srl.visgo.gui.Login;
import srl.visgo.util.chat.ChatManager;
import srl.visgo.util.chat.MessageProcessor;
import srl.visgo.util.chat.listeners.CommandMessageListener;
import srl.visgo.util.chat.listeners.GroupMessageListener;
import srl.visgo.util.chat.listeners.IndividualMessageListener;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import gDocsFileSystem.GDatabase;

public class Data {
	private DocumentRoot documentRoot;
	private GDatabase database;
	private ChatManager chatManager;
	private HashMap<String,Collaborator> collaborators;
	private DocsService docsService;
	private LinkedList<DataListener> listeners;
	private MessageProcessor messageProcessor;

	public Data(){
		
		listeners = new LinkedList<DataListener>();
		collaborators = new HashMap<String,Collaborator>();
		docsService = new DocsService("VISGO-V1");
		try {
			docsService.setUserCredentials(Login.username, Login.password);
			chatManager = new ChatManager(Login.username, Login.password);
			messageProcessor = chatManager.getMessageInterpreter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DocumentRoot getDocumentRoot(){
		return documentRoot;
	}
	
	public Collaborator getCollaborator(String username){
		return collaborators.get(username);
	}
	public Collection<Collaborator> getAllCollaborators(){
		return collaborators.values();
	}
	public synchronized void fireDataChange(DataEventType type){
		for(DataListener listener:listeners){
			listener.onDataUpdate(type);
		}
	}
	public void addDataListener(DataListener listener){
		listeners.add(listener);
	}
	
	public void addIndividualMessageListener(IndividualMessageListener listener){
		messageProcessor.addIndividualMessageListener(listener);
	}
	public void addGroupMessageListener(GroupMessageListener listener){
		messageProcessor.addGroupMessageListener(listener);
	}
	public void addCommandMessageListener(CommandMessageListener listener){
		messageProcessor.addCommandMessageListener(listener);
	}
}
