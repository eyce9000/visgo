package srl.visgo.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.PresentationEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
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
				}
			}
			else{
				doc.setListEntry(listEntry);
			}

		}
	}
	public void load(String title) throws IOException, ServiceException{
		URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/?showfolders=false");
		DocumentQuery query = new DocumentQuery(feedUri);
		query.setTitleQuery(title);
		query.setTitleExact(true);
		DocumentListFeed feed = docsService.getFeed(query,DocumentListFeed.class);
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
				}
			}
			else{
				doc.setListEntry(listEntry);
			}

		}
	}
	public synchronized void updateAllRevisionHistory(){
		for(Document doc :mDocuments.values()){
			if(doc.getId()!=null){
				try {
					Document.updateRevisionHistory(doc, docsService);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	/**
	 * Creates a blank document of the given type
	 * @param documentType The type of document to create
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentListEntry createDocument(String documentType) throws MalformedURLException, IOException, ServiceException
	{
		DocumentListEntry newEntry = null;

		if(documentType == "Document")
		{
			newEntry = new DocumentEntry();
		}
		else if(documentType == "Presentation")
		{
			newEntry = new PresentationEntry();
		}
		else if(documentType == "Spreadsheet")
		{
			newEntry = new SpreadsheetEntry();
		}
		//else if(action == "Drawing")
		//{
		//	newEntry = new DrawingEntry();
		//}
		else
		{
			throw new ServiceException("Invalid document type specified");
		}

		newEntry.setTitle(new PlainTextConstruct("New " + documentType));
		return uploadDocument(newEntry);
	}
	
	/**
	 * Tells Google to create a document in Google Docs
	 * @param entry The document to create
	 * @return A DocumentListEntry containing the new document's information
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentListEntry uploadDocument(DocumentListEntry entry) throws MalformedURLException, IOException, ServiceException
	{
		return docsService.insert(new URL("https://docs.google.com/feeds/default/private/full/"), entry);
	}
	
	/**
	 * Tells Google to add a new Access Control role for the given document
	 * @param role The role to add
	 * @param scope The scope of the role (user type and user name)
	 * @param entry The document to add the role to
	 * @return The new ACL entry data
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public AclEntry addAclRole(AclRole role, AclScope scope, DocumentListEntry entry) throws MalformedURLException, IOException, ServiceException
	{
		AclEntry aclEntry = new AclEntry();
		aclEntry.setRole(role);
		aclEntry.setScope(scope);
		
		return docsService.insert(new URL(entry.getAclFeedLink().getHref()), aclEntry);
	}
}
