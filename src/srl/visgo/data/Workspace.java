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


		Pair<String,String> pair;

		List<Pair<String,String>> folders = mFileSystem.getRootFolders();
		List<Pair<String,String>> files = mFileSystem.getRootFiles();

		for(Pair<String,String> folder : folders){
			DocumentGroup group = new DocumentGroup(folder.second,folder.first);
			lookupChildren(group);
			rootGroups.put(group.getId(), group);
		}

		for(Pair<String,String> file: files){
			Document doc = mDocumentList.getDocumentById(file.first);
			rootDocuments.put(doc.getId(), doc);
		}
	}

	private void lookupChildren(DocumentGroup group) throws Exception{
		List<Pair<String,String>> childrenFiles = mFileSystem.getChildrenFiles(group);
		List<Pair<String,String>> childrenFolders = mFileSystem.getChildrenFolders(group);
		System.out.println("Group: "+group.getName());

		for(Pair<String,String> file: childrenFiles){
			Document childDoc = mDocumentList.getDocumentById(file.first);
			if(childDoc!=null){
				group.addDocument(childDoc);
				System.out.println("Doc: "+childDoc.getName());
			}
		}

		for(Pair<String,String> folder: childrenFolders){
			DocumentGroup childGroup = new DocumentGroup(folder.second,folder.first);
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
}
