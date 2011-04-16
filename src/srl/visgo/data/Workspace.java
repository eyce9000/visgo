package srl.visgo.data;

import gDocsFileSystem.GDatabase;
import gDocsFileSystem.GFileSystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;

public class Workspace {

	DocumentList mDocumentList;
	GDatabase mDatabase;
	GFileSystem mFileSystem;

	HashMap<String,Document> rootDocuments = new HashMap<String,Document>();
	HashMap<String,DocumentGroup> rootGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,DocumentGroup> allGroups = new HashMap<String,DocumentGroup>();

	public Workspace(DocumentList docList, GDatabase database) throws Exception {
		mDocumentList = docList;
		mDatabase = database;
		mFileSystem = new GFileSystem(database);

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
}
