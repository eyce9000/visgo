package srl.visgo.data;

import com.google.gdata.data.docs.DocumentListEntry;

public class Document implements Entry {
	DocumentListEntry mEntry;
	DocumentGroup mParent = null;

	public Document(DocumentListEntry entry){
		mEntry = new DocumentListEntry(entry);
	}
	
	public String getName(){
		return mEntry.getTitle().getPlainText();
	}

	@Override
	public DocumentListEntry getListEntry() {
		return mEntry;
	}

	@Override
	public String getHref() {
		return mEntry.getDocumentLink().getHref();
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
	public String getDocId() {
		return mEntry.getDocId();
	}
}
