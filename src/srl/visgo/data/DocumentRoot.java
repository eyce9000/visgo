package srl.visgo.data;

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

public class DocumentRoot {
	HashMap<String,Entry> mEntries = new HashMap<String,Entry>();
	HashMap<String,Document> mDocuments = new HashMap<String,Document>();
	HashMap<String,DocumentGroup> mDocumentGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,DocumentGroup> mRootDocumentGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,Document> mRootCategoryDocuments = new HashMap<String,Document>();
	DocsService docsService;
	GFileSystem mFileSystem;
	public DocumentRoot(GFileSystem fileSystem) throws IOException, ServiceException {
		mFileSystem = fileSystem;
		reload();
	}
	public void reload() throws IOException, ServiceException{
		URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/?showfolders=true");
		DocumentListFeed feed = docsService.getFeed(feedUri, DocumentListFeed.class);
		for (DocumentListEntry listEntry : feed.getEntries()) {
			if(listEntry.getType().equals("folder")){
				//This is a document group
				DocumentGroup group = mDocumentGroups.get(listEntry.getDocId());
				if(group == null){
					group = new DocumentGroup(listEntry);
					mEntries.put(group.getDocId(),group);
					mDocumentGroups.put(group.getDocId(),group);
				}
				else{
					group.setListEntry(listEntry);
				}
			}
			else{
				//This is a document
				Document doc = mDocuments.get(listEntry.getDocId());
				if(doc == null){
					doc = new Document(listEntry);
					mEntries.put(listEntry.getDocId(), doc);
					mDocuments.put(doc.getHref(), doc);
				}
				else{
					doc.setListEntry(listEntry);
				}
			}
		}
		for(DocumentListEntry listEntry : feed.getEntries()){
			Entry entry = mEntries.get(listEntry.getDocId());
			if (!listEntry.getParentLinks().isEmpty()) {
				for (Link link : listEntry.getParentLinks()) {
					//System.out.println(link.getHref());
					String[] split = link.getHref().split("folder%3A");
					if(split.length==2){
						DocumentGroup group = mDocumentGroups.get(split[1]);
						if(group!=null)
							group.addEntry(entry);
					}
				}
			}
			else{
				if(entry instanceof Document){
					Document doc = (Document) entry;
					mRootCategoryDocuments.put(doc.getDocId(), doc);
				}
			}
		}
		for(DocumentGroup group: mDocumentGroups.values()){
			if(!group.hasParent()){
				mRootDocumentGroups.put(group.getName(),group);
			}
		}
	}
	public Collection<DocumentGroup> getRootDocumentGroups(){
		return mRootDocumentGroups.values();
	}
	public Collection<Document> getRootDocuments(){
		return mRootCategoryDocuments.values();
	}
}
