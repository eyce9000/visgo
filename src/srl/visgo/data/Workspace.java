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

import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.CommandMessageListener;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
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

	public Workspace(DocumentList docList, GDatabase database) throws Exception {
		mDocumentList = docList;
		mDatabase = database;
		mFileSystem = new GFileSystem(database);
		
		saver = new DataSaver(mFileSystem);
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
			rootDocuments.put(doc.getGoogleId(), doc);
		}
	}

	private void lookupChildren(DocumentGroup group) throws Exception{
		List<Document> childrenFiles = mFileSystem.getChildrenFiles(group);
		List<DocumentGroup> childrenFolders = mFileSystem.getChildrenFolders(group);
		System.out.println("Group: "+group.getName());

		for(Document file: childrenFiles){
			Document childDoc = mDocumentList.getDocumentByGoogleId(file.getGoogleId());
			if(childDoc!=null){
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
		if(name.equals("documentMoved")){
			try {
				Map<String,Object> map = mapper.readValue(body, Map.class);
				Document tempDoc = Document.deserializeShallow(map);
				Document ourDoc = getDocumentById(tempDoc.getId());
				if(ourDoc.hasParent()){
					ourDoc.getParent().removeDocument(ourDoc);
				}
				
				ourDoc.copyValues(tempDoc);
				
				DocumentGroup newParent = allGroups.get(tempDoc.getParentId());
				if(newParent!=null){
					newParent.addDocument(ourDoc);
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
}
