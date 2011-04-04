package srl.visgo.data;

import gDocsFileSystem.GDatabase;
import gDocsFileSystem.GFileSystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;

public class Workspace {
	
	DocumentList mDocumentList;
	GDatabase mDatabase;
	GFileSystem mFileSystem;
	public Workspace(DocumentList docList, GDatabase database) throws Exception {
		mDocumentList = docList;
		mDatabase = database;
		mFileSystem = new GFileSystem(database);
	}
	
	public Collection<DocumentGroup> getRootDocumentGroups(){
		return mDocumentList.getRootDocumentGroups();
	}
}
