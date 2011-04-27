package srl.visgo.data;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Semaphore;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import srl.visgo.gui.Login;
import srl.visgo.gui.Visgo;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Person;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.RevisionEntry;
import com.google.gdata.data.docs.RevisionFeed;
import com.google.gdata.util.ServiceException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Document implements Entry {
	DocumentListEntry mEntry;
	DocumentGroup mParent = null;
	private String mName;
	private String mGoogleId;
	private String mId;
	private String mParentId = "0";
	private double mOffsetX;
	private double mOffsetY;
	private Calendar mLastModified;
	private String mLastModifiedBy;
	private Semaphore modifyRevHistory = new Semaphore(1,true);
	private Map<String,Revision> revisionHistory = new HashMap<String,Revision>();

	private Document(){
		mLastModified = GregorianCalendar.getInstance();
	}

	private Document(Document doc){
		this();
		this.copyValues(doc);
	}

	public Document(String name,String id,String googleId){
		this();
		mName = name;
		mId = id;
		mGoogleId = googleId;
	}

	public Document(DocumentListEntry entry){
		this();
		setListEntry(entry);
	}

	public String getName(){
		if(mEntry != null)
			return mEntry.getTitle().getPlainText();
		else
			return mName;
	}
	public DocumentListEntry getListEntry() {
		return mEntry;
	}
	public String getHref() {
		if(mEntry!=null)
			return mEntry.getDocumentLink().getHref();
		else
			return "";
	}
	public double getOffsetX(){
		return mOffsetX;
	}
	public void setOffsetX(double offsetX){
		mOffsetX = offsetX;
	}
	public double getOffsetY(){
		return mOffsetY;
	}
	public void setOffsetY(double offsetY){
		mOffsetY = offsetY;
	}

	@Override
	public void setParent(DocumentGroup parent) {
		mParent = parent;
		if(parent == null){
			mParentId = "0";
		}
	}


	@Override
	public DocumentGroup getParent() {
		return mParent;
	}

	@Override
	public boolean hasParent() {
		return mParent!=null;
	}

	@Override
	public String getId(){
		return mId;
	}

	@Override
	public void setId(String id) {
		mId = id;
	}
	@Override
	public String getParentId(){
		if(hasParent()){
			return mParent.getId();
		}
		else{
			return mParentId;
		}
	}
	public String getGoogleId(){
		if(mEntry!=null)
			return mEntry.getDocId();
		else
			return mGoogleId;
	}
	public void setListEntry(DocumentListEntry entry) {
		this.mEntry = new DocumentListEntry(entry);
	}
	@Override
	public Collection<Revision> getRevisionHistory(){
		modifyRevHistory.acquireUninterruptibly();
		Collection<Revision> rev = Collections.synchronizedCollection(revisionHistory.values());
		modifyRevHistory.release();
		return rev;
	}
	public void save(){
		mLastModified = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-0"));
		mLastModifiedBy = Login.username;
		if(Visgo.data != null)
			Visgo.data.entryUpdated(this);
	}
	public void copyValues(Document doc){
		this.mId = doc.getId();
		this.mGoogleId = doc.getGoogleId();
		this.mName = doc.getName();
		this.mParentId = doc.getParentId();
		this.mParent = doc.getParent();
		this.mOffsetX = doc.getOffsetX();
		this.mOffsetY = doc.getOffsetY();
	}

	public Document clone(){
		return new Document(this);
	}

	public static Map serialize(Document doc){
		Map m = new HashMap();
		m.put("filename", doc.getName());
		m.put("fileid",doc.mId);
		m.put("gfid",doc.getGoogleId());
		m.put("offsetX", doc.mOffsetX+"");
		m.put("offsetY", doc.mOffsetY+"");
		m.put("modifiedTime", doc.mLastModified.getTimeInMillis()+"");
		m.put("modifiedBy", doc.mLastModifiedBy);
		m.put("parentfolder",doc.getParentId());
		m.put("class", doc.getClass().getName());
		return m;
	}
	public static Document deserializeShallow(Map m){
		Document doc = new Document((String)m.get("filename"),(String)m.get("fileid"),(String)m.get("gfid"));
		doc.setOffsetX(Double.parseDouble(m.get("offsetX").toString()));
		doc.setOffsetY(Double.parseDouble(m.get("offsetY").toString()));
		doc.mParentId = (String)m.get("parentfolder");
		doc.mLastModified = new GregorianCalendar(TimeZone.getTimeZone("GMT-0"));
		doc.mLastModified.setTimeInMillis(Long.parseLong(m.get("modifiedTime").toString()));
		doc.mLastModifiedBy = (String) m.get("modifiedBy");
		return doc;
	}

	public static void updateRevisionHistory(Document doc, DocsService service) throws IOException, ServiceException{

		URL url = new URL(doc.mEntry.getSelfLink().getHref() + "/revisions?reverse=true");
		RevisionFeed revisionFeed = service.getFeed(url, RevisionFeed.class);

		Calendar now = GregorianCalendar.getInstance();
		Calendar gmt = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		now.setTimeInMillis(System.currentTimeMillis());
		doc.modifyRevHistory.acquireUninterruptibly();
		doc.revisionHistory.clear();
		List<RevisionEntry> entries = new ArrayList<RevisionEntry>(revisionFeed.getEntries());
		Collections.reverse(entries);
		
		System.out.println("Looking up revisions for "+doc.getName());
		
		for (RevisionEntry entry : entries) {
			
			Revision rev = Revision.createRevision(entry);
			
			long diff = now.getTimeInMillis()-rev.getModifiedTime();
			
			
			if(diff > 300000)
				break;
			/*
			System.out.println(" -- " + entry.getTitle().getPlainText());
			System.out.println(", created on " + entry.getUpdated().toStringRfc822() +" -- "+entry.getUpdated().getTzShift()+ " ");
			System.out.println(" by " + entry.getModifyingUser().getName() + " - "
					+ entry.getModifyingUser().getEmail() + "\n");
			*/
			

			System.out.println("--Modified at:"+(diff/60000.0f)+" by:"+entry.getModifyingUser().getName());
			String contributors = "";
			
			for(Person contrib : entry.getAuthors()){
				contributors +=" "+contrib.getEmail();
			}
			System.out.println(contributors);
			
			if(!doc.revisionHistory.containsKey(rev.getModifiedByUsername())){
				doc.revisionHistory.put(rev.getModifiedBy().getUsername(),rev);
			}
			else{
				Revision prevRev = doc.revisionHistory.get(rev.getModifiedByUsername());
				if(prevRev.compareTo(rev)<0){
					doc.revisionHistory.put(rev.getModifiedByUsername(), rev);
				}
			}
		}
		doc.modifyRevHistory.release();
	}

}
