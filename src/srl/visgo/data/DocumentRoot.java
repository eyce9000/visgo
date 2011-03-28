package srl.visgo.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;

public class DocumentRoot {
	HashMap<String,Document> mDocuments = new HashMap<String,Document>();
	HashMap<String,DocumentGroup> mDocumentGroups = new HashMap<String,DocumentGroup>();
	HashMap<String,Document> mNoCategoryDocuments = new HashMap<String,Document>();
	public DocumentRoot(DocsService service) throws IOException, ServiceException {

		URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/?showfolders=true");
		DocumentListFeed feed = service.getFeed(feedUri, DocumentListFeed.class);
		HashMap<String,Entry> entries = new HashMap<String,Entry>();
		HashMap<String,DocumentGroup> groups = new HashMap<String,DocumentGroup>();
		for (DocumentListEntry listEntry : feed.getEntries()) {
			if(listEntry.getType().equals("folder")){
				//This is a document group
				DocumentGroup group = new DocumentGroup(listEntry);
				entries.put(group.getDocId(),group);
				groups.put(group.getDocId(),group);
				//System.out.println(group.getDocId());
			}
			else{
				//This is a document
				Document doc = new Document(listEntry);
				entries.put(listEntry.getDocId(), doc);
				mDocuments.put(doc.getHref(), doc);
			}
		}
		for(DocumentListEntry listEntry : feed.getEntries()){
			Entry entry = entries.get(listEntry.getDocId());
			if (!listEntry.getParentLinks().isEmpty()) {
				for (Link link : listEntry.getParentLinks()) {
					//System.out.println(link.getHref());
					String[] split = link.getHref().split("folder%3A");
					if(split.length==2){
						DocumentGroup group = groups.get(split[1]);
						if(group!=null)
							group.addEntry(entry);
					}
				}
			}
			else{
				if(entry instanceof Document){
					Document doc = (Document) entry;
					mNoCategoryDocuments.put(doc.getDocId(), doc);
				}
			}
		}
		for(DocumentGroup group: groups.values()){
			if(!group.hasParent()){
				mDocumentGroups.put(group.getName(),group);
			}
		}
	}
	public Collection<DocumentGroup> getRootDocumentGroups(){
		return mDocumentGroups.values();
	}
	public Collection<Document> getRootDocuments(){
		return mNoCategoryDocuments.values();
	}
}
