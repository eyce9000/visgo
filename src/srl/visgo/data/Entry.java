package srl.visgo.data;

import com.google.gdata.data.docs.DocumentListEntry;

public interface Entry {
	public String getName();
	public void setParent(DocumentGroup parent);
	public DocumentGroup getParent();
	public boolean hasParent();
	public String getId();
}
