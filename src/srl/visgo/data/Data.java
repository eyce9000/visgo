package srl.visgo.data;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import srl.visgo.gui.Login;
import srl.visgo.util.chat.ChatManager;
import srl.visgo.util.chat.MessageProcessor;
import srl.visgo.util.chat.listeners.CommandMessageListener;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.GroupMessageListener;
import srl.visgo.util.chat.listeners.IndividualMessageListener;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Person;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import gDocsFileSystem.GDatabase;

public class Data {
	public Workspace workspace;
	private GDatabase mDatabase;
	private Document workspaceDoc;
	private ChatManager chatManager;
	private HashMap<String,Collaborator> mCollaborators;
	private DocsService docsService;
	private LinkedList<DataListener> listeners;
	private MessageProcessor messageProcessor;
	private DocumentList mDocumentList;

	public Data(){
		mDatabase = new GDatabase();

		listeners = new LinkedList<DataListener>();
		mCollaborators = new HashMap<String,Collaborator>();
		docsService = new DocsService("VISGO-V1");
		try {
			docsService.setUserCredentials(Login.username, Login.password);
			loginToChat();
			mDocumentList = new DocumentList(docsService);
			selectDatabase();
			workspace = new Workspace(mDocumentList,mDatabase);
			updateCollaborators();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loginToChat(){
		chatManager = new ChatManager(Login.username, Login.password);
		try
		{
			//connecting to gtalk 
			chatManager.connect();		
		}
		catch (XMPPException xe){

			System.out.println("Cannot Connect to the gtalk server :");
		}
		catch (IllegalStateException ie){

			System.out.println("Cannot Connect to the gtalk server :");
		}
		messageProcessor = chatManager.getMessageInterpreter();
	}

	private void updateCollaborators() throws MalformedURLException, IOException, ServiceException,Exception{
		AclFeed aclFeed = docsService.getFeed(new URL(workspaceDoc.getListEntry().getAclFeedLink().getHref()), AclFeed.class);
		HashSet<String> tempCollaborators = new HashSet<String>();
		for (AclEntry entry : aclFeed.getEntries()) {
			String email = entry.getScope().getValue();
			String rolename = entry.getRole().getValue();
			tempCollaborators.add(email.split("@")[0]);
		}
		List<String>columns = Arrays.asList(new String[]{"userid","gid","realname","color"});

		Map<String,ArrayList<String>> results= mDatabase.select("collaborators", columns, null);
		for(String gid : results.get("gid")){
			tempCollaborators.remove(gid);
		}


		int currentUserId = results.get("gid").size();
		boolean reloadResults = false;
		for(String collaborator:tempCollaborators){
			List<String> values = Arrays.asList(new String[]{
					currentUserId+"",
					collaborator,
					collaborator,"#ffffff"});
			System.out.println("Adding collaborator "+collaborator);
			mDatabase.insert("collaborators",columns,values);
			currentUserId ++;
			reloadResults = true;
		}
		if(reloadResults)
			results = mDatabase.select("collaborators", columns, null);
		List<String>gids = results.get("gid");
		List<String>colors = results.get("color");



		mCollaborators = new HashMap<String, Collaborator>();
		for(int i=0; i<gids.size(); i++){
			String gid = gids.get(i)+"@gmail.com";
			String colorStr = colors.get(i);
			System.out.println(colorStr);
			Color color = Color.decode(colorStr);
			//if(!gid.equals("eyce9000@gmail.com"))
			//	continue;
			Collaborator collab = new Collaborator(gid,color);
			
			mCollaborators.put(collab.getUsername(),collab);
			if(!gid.equals(Login.username)){
				chatManager.createChat(collab.getUsername(), i+"", null);
			}
		}

	}

	private void selectDatabase(){
		mDatabase = new GDatabase();
		Collection<Document> databases = mDocumentList.getVisgoDatabases();
		if(databases.size() == 0){
			//TODO create new database
			System.out.println("No database found");
			System.exit(0);
		}
		else if(databases.size() == 1){
			workspaceDoc = (Document)databases.toArray()[0];
			try{
				mDatabase.setDatabase(workspaceDoc.getName());
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		else{
			//TODO select from multiple databases
			System.out.println("More than one database found");
			System.exit(0);
		}

	}

	public Collaborator getCollaborator(String username){
		return mCollaborators.get(username);
	}
	public Collection<Collaborator> getAllCollaborators(){
		return mCollaborators.values();
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

	public GroupMessage sendGroupMessage(String text){
		GroupMessage message = new GroupMessage(new Message(),text);
		return chatManager.sendGroupMessage(message);
	}
}
