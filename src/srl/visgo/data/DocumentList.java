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
	HashMap<String,Document> mDocuments = new HashMap<String,Document>();
	HashMap<String,Document> mDocsById = new HashMap<String,Document>();
	HashMap<String,Document> mVisgoDatabases = new HashMap<String,Document>();
	DocsService docsService;
	public DocumentList(DocsService service) throws IOException, ServiceException{
		docsService = service;
		reload();
	}
	public void reload() throws IOException, ServiceException{
		URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/?showfolders=false");
		DocumentListFeed feed = docsService.getFeed(feedUri, DocumentListFeed.class);
		for (DocumentListEntry listEntry : feed.getEntries()) {
			//System.out.println(listEntry.getTitle().getPlainText());

			//This is a document
			Document doc = mDocuments.get(listEntry.getDocId());
			if(doc == null){
				doc = new Document(listEntry);
				if(doc.getName().endsWith(".workspace")){
					if(!mVisgoDatabases.containsKey(doc.getGoogleId())){
						mVisgoDatabases.put(doc.getGoogleId(), doc);
					}
				}
				else{
					mDocuments.put(doc.getGoogleId(), doc);
					mDocsById.put(doc.getId(), doc);
					//System.out.println(doc.getName());
					System.out.println(doc.getGoogleId());
				}
			}
			else{
				doc.setListEntry(listEntry);
			}

		}
	}
	public Collection<Document> getDocuments(){
		return mDocuments.values();
	}
	public Collection<Document> getVisgoDatabases(){
		return mVisgoDatabases.values();
	}
	public Document getDocumentByGoogleId(String id){
		return mDocuments.get(id);
	}
	public Document getDocumentById(String id){
		return mDocsById.get(id);
	}
}
