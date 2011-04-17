package srl.visgo.data;

import java.util.HashMap;
import java.util.Map;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

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
	
	public static Map serialize(Document doc){
		Map m = new HashMap();
		m.put("name", doc.mName);
		m.put("fileid",doc.mId);
		m.put("gid",doc.getGoogleId());
		m.put("offsetX", doc.mOffsetX);
		m.put("offsetY", doc.mOffsetY);
		if(doc.hasParent()){
			m.put("parentid",doc.getParent().getId());
		}
		return m;
	}
	public static Document deserializeShallow(Map m){
		Document doc = new Document((String)m.get("name"),(String)m.get("fileid"),(String)m.get("gfid"));
		doc.setOffsetX(Double.parseDouble(m.get("offsetX").toString()));
		doc.setOffsetY(Double.parseDouble(m.get("offsetY").toString()));
		doc.mParentId = (String)m.get("parentid");
		return doc;
	}
	public static Document deserialize(Map m, Workspace w){
		Document doc = w.getDocumentById((String)m.get("fildid"));
		if(doc==null){
			doc = new Document((String)m.get("name"),(String)m.get("fileid"),(String)m.get("gfid"));
		}
		doc.setOffsetX(Double.parseDouble(m.get("offsetX").toString()));
		doc.setOffsetY(Double.parseDouble(m.get("offsetY").toString()));
		String parentid = (String)m.get("parentid");
		if(doc.mParentId!=null){
			doc.setParent(w.getDocumentGroupById(doc.mParentId));
		}
		return doc;
	}
}
