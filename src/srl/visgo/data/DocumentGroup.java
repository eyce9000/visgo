package srl.visgo.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import srl.visgo.gui.zoom.PDocumentGroup;

import com.google.gdata.data.docs.DocumentListEntry;

public class DocumentGroup implements Entry{
	HashMap<String,Document> mDocuments;
	HashMap<String,DocumentGroup> mSubGroups;
	String mName;
	String mId;
	DocumentGroup mParent;
	double mOffsetX,mOffsetY;
	
	
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
		document.mParent = this;
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
		mDocuments.remove(document.getName());
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
	
}
