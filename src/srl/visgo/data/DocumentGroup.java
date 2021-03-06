package srl.visgo.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import srl.visgo.gui.Login;
import srl.visgo.gui.Visgo;
import srl.visgo.gui.zoom.PDocumentGroup;

import com.google.gdata.data.docs.DocumentListEntry;

public class DocumentGroup implements Entry{
	HashMap<String,Document> mDocuments;
	HashMap<String,DocumentGroup> mSubGroups;
	String mName;
	String mId;
	String mParentId = "0";
	DocumentGroup mParent;
	double mOffsetX,mOffsetY;
	private Calendar mLastModified;
	private String mLastModifiedBy;

	private DocumentGroup(DocumentGroup group){
		this.copyValues(group);
	}
	private DocumentGroup(String name) {
		mName = name;
		mSubGroups = new HashMap<String,DocumentGroup>();
		mDocuments = new HashMap<String,Document>();
	}
	private DocumentGroup(String name,String id){
		this(name);
		mId = id;
	}

	public String getName(){
		return mName;
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




	public void addDocument(Document document){
		mDocuments.put(document.getName(),document);
		document.setParent(this);
	}
	public void addDocuments(Document[] documents){
		for(Document doc : documents){
			addDocument(doc);
		}
	}
	public Collection<Entry> getRootEntries(){
		ArrayList<Entry> rootEntries = new ArrayList<Entry>();
		rootEntries.addAll(mDocuments.values());
		rootEntries.addAll(mSubGroups.values());
		return rootEntries;
	}
	public Collection<Document> getDocuments(){
		return mDocuments.values();
	}

	public Collection<DocumentGroup> getSubGroups(){
		return mSubGroups.values();
	}
	public int size(){
		int size = 0;
		size += topSize();
		for(DocumentGroup group:mSubGroups.values()){
			size += group.size();
		}
		return size;
	}
	public int topSize(){
		return mDocuments.size();
	}

	public void addSubGroup(DocumentGroup group){
		mSubGroups.put(group.getName(),group);
	}
	public void addEntry(Entry entry){
		entry.setParent(this);
		if(entry instanceof Document){
			addDocument((Document)entry);
		}
		else if(entry instanceof DocumentGroup){
			addSubGroup((DocumentGroup) entry);
		}
	}

	public void removeDocument(Document document){
		if(document!=null){
			mDocuments.remove(document.getName());
			document.setParent(null);
		}
	}

	@Override
	public void setParent(DocumentGroup parent) {
		mParent = parent;
	}
	@Override
	public DocumentGroup getParent() {
		return mParent;
	}
	@Override
	public boolean hasParent() {
		return mParent != null;
	}

	public void copyValues(DocumentGroup group){
		this.mDocuments = group.mDocuments;
		this.mId = group.getId();
		this.mName = group.getName();
		this.mOffsetX = group.getOffsetX();
		this.mOffsetY = group.getOffsetY();
		this.mParentId = group.getParentId();
		this.mParent = group.getParent();
		this.mSubGroups = group.mSubGroups;
	}

	public Document getDocument(String path){
		String[] split = path.split("/");
		if(split.length>1){
			if(mSubGroups.containsKey(split[0])){
				DocumentGroup subGroup = mSubGroups.get(split[0]);
				String newPath = "";
				for(int i=1; i<split.length; i++){
					newPath += split[i];
					if(i<split.length-1)
						newPath += "/";
				}
				return subGroup.getDocument(newPath);
			}
			else{
				return null;
			}
		}
		else if(split.length==1){
			return mDocuments.get(split[0]);
		}
		else{
			return null;
		}
	}
	@Override
	public String getId() {
		return mId;
	}

	@Override
	public void setId(String id) {
		mId = id;
	}
	
	@Override
	public void save(){
		mLastModified = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-0"));
		mLastModifiedBy = Login.username;
		if(Visgo.data!=null)
			Visgo.data.entryUpdated(this);
		for(Document doc: mDocuments.values()){
			doc.save();
		}
	}
	
	@Override
	public String getParentId(){
		if(mParent!=null)
		{
			return mParent.getId();
		}
		else{
			return mParentId;
		}
	}
	@Override
	public Collection<Revision> getRevisionHistory() {
		HashMap<String,Revision> revisions = new HashMap<String,Revision>();
		for(Document doc: mDocuments.values()){
			for(Revision rev: doc.getRevisionHistory()){
				String username = rev.getModifiedByUsername();
				if(revisions.containsKey(username)){
					//Already exists
					Revision prevRev = revisions.get(username);
					if(prevRev.compareTo(rev) < 0){ //More recent
						revisions.put(username, rev);
					}
				}
				else{
					revisions.put(username, rev);
				}
			}
		}
		return revisions.values();
	}
	@Override
	public Entry clone(){
		return new DocumentGroup(this);
	}
	
	public static DocumentGroup createGroup(String name){
		DocumentGroup group = new DocumentGroup(name);
		group.save();
		return group;
	}

	public static Map serialize(DocumentGroup group){
		Map m = new HashMap();
		m.put("foldername", group.mName);
		m.put("folderid", group.mId);
		m.put("offsetX", group.mOffsetX+"");
		m.put("offsetY", group.mOffsetY+"");
		m.put("parentfolder", group.getParentId());
		m.put("modifiedTime",group.mLastModified.getTimeInMillis()+"");
		m.put("modifiedBy", group.mLastModifiedBy);
		m.put("class", group.getClass().getName());
		return m;
	}

	public static DocumentGroup deserializeShallow(Map m){
		DocumentGroup group = new DocumentGroup((String)m.get("foldername"),(String)m.get("folderid"));
		group.setOffsetX(Double.parseDouble(m.get("offsetX").toString()));
		group.setOffsetY(Double.parseDouble(m.get("offsetY").toString()));
		group.mParentId =(String) m.get("parentfolder");
		group.mLastModified = new GregorianCalendar(TimeZone.getTimeZone("GMT-0"));
		group.mLastModified.setTimeInMillis(Long.parseLong(m.get("modifiedTime").toString()));
		group.mLastModifiedBy = (String) m.get("modifiedBy");
		return group;
	}
}
