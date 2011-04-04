package srl.visgo.data;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;

public class DocumentList {
	HashMap<String,Entry> mEntries = new HashMap<String,Entry>();
	HashMap<String,Document> mDocuments = new HashMap<String,Document>();
	HashMap<String,DocumentGroup> mDocumentGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,DocumentGroup> mRootDocumentGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,Document> mRootDocuments = new HashMap<String,Document>();
	HashMap<String,Document> mVisgoDatabases = new HashMap<String,Document>();
	DocsService docsService;
	public DocumentList(DocsService service) throws IOException, ServiceException{
		docsService = service;
		reload();
	}
	public void reload() throws IOException, ServiceException{
		URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/?showfolders=true");
		DocumentListFeed feed = docsService.getFeed(feedUri, DocumentListFeed.class);
		for (DocumentListEntry listEntry : feed.getEntries()) {
			//System.out.println(listEntry.getTitle().getPlainText());
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
					if(doc.getName().endsWith(".workspace")){
						if(!mVisgoDatabases.containsKey(doc.getDocId())){
							mVisgoDatabases.put(doc.getDocId(), doc);
							//System.out.println(doc.getName());
						}
					}
					else{
						mEntries.put(listEntry.getDocId(), doc);
						mDocuments.put(doc.getHref(), doc);
						//System.out.println(doc.getName());
					}
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
					mRootDocuments.put(doc.getDocId(), doc);
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
		return mRootDocuments.values();
	}
	public Collection<Document> getVisgoDatabases(){
		return mVisgoDatabases.values();
	}
}
