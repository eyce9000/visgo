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
	String mParentId;
	DocumentGroup mParent;
	double mOffsetX,mOffsetY;
	private Calendar mLastModified;
	private String mLastModifiedBy;

	private DocumentGroup(DocumentGroup group){
		this.copyValues(group);
	}
	public DocumentGroup(String name) {
		mName = name;
		mSubGroups = new HashMap<String,DocumentGroup>();
		mDocuments = new HashMap<String,Document>();
	}
	public DocumentGroup(String name,String id){
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
		modified();
	}
	public void addDocuments(Document[] documents){
		for(Document doc : documents){
			addDocument(doc);
		}
		modified();
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
		modified();
	}
	public void addEntry(Entry entry){
		entry.setParent(this);
		if(entry instanceof Document){
			addDocument((Document)entry);
		}
		else if(entry instanceof DocumentGroup){
			addSubGroup((DocumentGroup) entry);
		}
		modified();
	}

	public void removeDocument(Document document){
		mDocuments.remove(document.getName());
		modified();
	}

	@Override
	public void setParent(DocumentGroup parent) {
		mParent = parent;
		modified();
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
	public Entry clone(){
		return new DocumentGroup(this);
	}

	private void modified(){
		mLastModified = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-0"));
		mLastModifiedBy = Login.username;
		if(Visgo.data!=null)
			Visgo.data.entryUpdated(this);
	}

	public static Map serialize(DocumentGroup group){
		Map m = new HashMap();
		m.put("foldername", group.mName);
		m.put("folderid", group.mId);
		m.put("offsetX", group.mOffsetX);
		m.put("offsetY", group.mOffsetY);
		m.put("parentfolder", group.getParentId());
		return m;
	}

	public static DocumentGroup deserializeShallow(Map m){
		DocumentGroup group = new DocumentGroup((String)m.get("foldername"),(String)m.get("folderid"));
		group.setOffsetX(Double.parseDouble(m.get("offsetX").toString()));
		group.setOffsetY(Double.parseDouble(m.get("offsetY").toString()));
		group.mParentId =(String) m.get("parentfolder");
		return group;
	}
}
