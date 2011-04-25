package srl.visgo.data;

import gDocsFileSystem.GDatabase;
import gDocsFileSystem.GFileSystem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import srl.visgo.gui.Visgo;
import srl.visgo.util.chat.ChatManager;
import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.CommandMessageListener;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;

public class Workspace implements CommandMessageListener{
	ObjectMapper mapper = new ObjectMapper();
	DocumentList mDocumentList;
	GDatabase mDatabase;
	GFileSystem mFileSystem;
	DataSaver saver;

	HashMap<String,Document> rootDocuments = new HashMap<String,Document>();
	HashMap<String,DocumentGroup> rootGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,DocumentGroup> allGroups = new HashMap<String,DocumentGroup>();

	public Workspace(DocumentList docList, GDatabase database, ChatManager manager) throws Exception {
		mDocumentList = docList;
		mDatabase = database;
		mFileSystem = new GFileSystem(database);

		saver = new DataSaver(mFileSystem,manager);
		new Thread(saver).start();

		List<DocumentGroup> folders = mFileSystem.getRootFolders();
		List<Document> files = mFileSystem.getRootFiles();

		for(DocumentGroup folder : folders){
			DocumentGroup group = folder;
			lookupChildren(group);
			rootGroups.put(group.getId(), group);
		}

		for(Document file: files){
			Document doc = mDocumentList.getDocumentByGoogleId(file.getGoogleId());
			doc.copyValues(file);
			rootDocuments.put(doc.getGoogleId(), doc);
		}
		manager.getMessageInterpreter().addCommandMessageListener(this);
	}

	private void lookupChildren(DocumentGroup group) throws Exception{
		List<Document> childrenFiles = mFileSystem.getChildrenFiles(group);
		List<DocumentGroup> childrenFolders = mFileSystem.getChildrenFolders(group);
		System.out.println("Group: "+group.getName());

		for(Document file: childrenFiles){
			Document childDoc = mDocumentList.getDocumentByGoogleId(file.getGoogleId());
			if(childDoc!=null){
				childDoc.copyValues(file);
				group.addDocument(childDoc);
				System.out.println("Doc: "+childDoc.getName());
			}
		}

		for(DocumentGroup folder: childrenFolders){
			DocumentGroup childGroup = folder;
			lookupChildren(childGroup);
		}
		allGroups.put(group.getId(),group);
	}

	public Collection<Document> getDocuments(){
		return mDocumentList.getDocuments();
	}
	public Collection<DocumentGroup> getRootDocumentGroups(){
		return rootGroups.values();
	}
	public Collection<Document> getRootDocuments(){
		return rootDocuments.values();
	}
	public Document getDocumentById(String id){
		return mDocumentList.getDocumentById(id);
	}
	public DocumentGroup getDocumentGroupById(String id){
		return allGroups.get(id);
	}
	public void saveEntry(Entry e){
		saver.saveEntry(e);
	}

	@Override
	public void CommandReceived(CommandMessage notification) {
		String name = notification.getCommandName();
		String body = notification.getArguments();
		if(name.equals("dataChanged")){
			//System.out.println(body);
			try {
				Map<String,Object> map = mapper.readValue(body, Map.class);
				String className = (String)map.get("class");
				if(className.equals("srl.visgo.data.Document")){
					Document tempDoc = Document.deserializeShallow(map);
					Document ourDoc = getDocumentById(tempDoc.getId());
					if(ourDoc==null){
						try {
							mDocumentList.load(tempDoc.getName());
							ourDoc = mDocumentList.getDocumentById(tempDoc.getId());
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					}
					if(ourDoc.hasParent()){
						ourDoc.getParent().removeDocument(ourDoc);
					}
					ourDoc.copyValues(tempDoc);
					DocumentGroup group = getDocumentGroupById(ourDoc.getParentId());
					if(group!=null){
						group.addDocument(ourDoc);
					}
				}
				else if(className.equals("srl.visgo.data.DocumentGroup")){
					DocumentGroup tempGroup = DocumentGroup.deserializeShallow(map);
					DocumentGroup ourGroup = getDocumentGroupById(tempGroup.getId());
					if(ourGroup==null){
						ourGroup = DocumentGroup.createGroup(tempGroup.getName());
					}
					ourGroup.copyValues(tempGroup);
					allGroups.put(ourGroup.getId(), ourGroup);
					if(ourGroup.getParentId().equals("0")){
						rootGroups.put(ourGroup.getId(), ourGroup);
					}
				}

			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * Creates a blank document of the given type
	 * @param documentType The type of document created
	 * @return success
	 */
	public boolean createDocument(String documentType)
	{
		try
		{
			DocumentListEntry newEntry = mDocumentList.createDocument(documentType);
			try
			{
				addCollaboratorRoles(newEntry);
			}
			catch(Exception e)
			{
				return false;
			}
			
			// TODO: Add icon to workspace
			Document doc = new Document(newEntry);
			mFileSystem.insertEntry(doc);
			Visgo.workspace.invalidate();
			
			return true;
		}
		catch(ServiceException e)
		{
			return false;
		}
		catch(Exception e1)
		{
			return false;
		}
	}

	/**
	 * Creates a document from an existing DocumentListEntry.
	 * This is used in uploading documents from the user's computer.
	 * @param entry A DocumentListEntry to create a new document from
	 * @return success
	 */
	public boolean createDocumentFromExisting(DocumentListEntry entry)
	{
		try
		{
			DocumentListEntry newEntry = mDocumentList.uploadDocument(entry);
			try
			{
				addCollaboratorRoles(newEntry);
			}
			catch(Exception e)
			{
				return false;
			}
			
			// TODO: Add icon to workspace
			Document doc = new Document(newEntry);
			mFileSystem.insertEntry(doc);
			Visgo.workspace.invalidate();
			
			return true;
		}
		catch(Exception e1)
		{
			return false;
		}
	}
	
	/**
	 * Gives all collaborators "writer" status on a given document 
	 * @param entry The document to add collaborators to
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void addCollaboratorRoles(DocumentListEntry entry) throws MalformedURLException, IOException, ServiceException
	{
		Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
		Collaborator self = Visgo.data.getCurrentCollaborator();
		
		for(Collaborator c : collaborators)
		{
			if(c == self)
				continue;

			AclRole role = new AclRole("writer");
			AclScope scope = new AclScope(AclScope.Type.USER, c.getUsername());
			mDocumentList.addAclRole(role, scope, entry);
		}
	}
}
