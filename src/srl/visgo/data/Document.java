package srl.visgo.data;

import com.google.gdata.data.docs.DocumentListEntry;

public class Document implements Entry {
	DocumentListEntry mEntry;
	DocumentGroup mParent = null;
	private String mName;
	private String mGoogleId;
	private String mId;
	private double mOffsetX;
	private double mOffsetY;

	public Document(String name,String id,String googleId){
		mName = name;
		mId = id;
		mGoogleId = googleId;
	}

	public Document(DocumentListEntry entry){
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
	public String getGoogleId(){
		if(mEntry!=null)
			return mEntry.getDocId();
		else
			return mGoogleId;
	}
	public void setListEntry(DocumentListEntry entry) {
		this.mEntry = new DocumentListEntry(entry);
		this.mParent = null;
	}
}
