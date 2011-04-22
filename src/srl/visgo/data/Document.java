package srl.visgo.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import srl.visgo.gui.Login;
import srl.visgo.gui.Visgo;

import com.google.gdata.data.docs.DocumentListEntry;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Document implements Entry {
	DocumentListEntry mEntry;
	DocumentGroup mParent = null;
	private String mName;
	private String mGoogleId;
	private String mId;
	private String mParentId;
	private double mOffsetX;
	private double mOffsetY;
	private Calendar mLastModified;
	private String mLastModifiedBy;

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
		modified();
	}
	public double getOffsetY(){
		return mOffsetY;
	}
	public void setOffsetY(double offsetY){
		mOffsetY = offsetY;
		modified();
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
	public void save(){
		modified();
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
	private void modified(){
		mLastModified = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-0"));
		mLastModifiedBy = Login.username;
		if(Visgo.data != null)
			Visgo.data.entryUpdated(this);
	}

	public Document clone(){
		return new Document(this);
	}

	public static Map serialize(Document doc){
		Map m = new HashMap();
		m.put("filename", doc.mName);
		m.put("fileid",doc.mId);
		m.put("gfid",doc.getGoogleId());
		m.put("offsetX", doc.mOffsetX+"");
		m.put("offsetY", doc.mOffsetY+"");
		m.put("modifiedTime", doc.mLastModified.getTimeInMillis()+"");
		m.put("modifiedBy", doc.mLastModifiedBy);
		if(doc.hasParent()){
			m.put("parentfolder",doc.getParentId());
		}
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


}
