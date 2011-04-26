package srl.visgo.data;

import java.util.Collection;

public interface Entry extends Cloneable {
	public String getName();
	public void setParent(DocumentGroup parent);
	public void setId(String id);
	public DocumentGroup getParent();
	public boolean hasParent();
	public String getId();
	public String getParentId();
	public double getOffsetX();
	public double getOffsetY();
	public Entry clone();
	public void save();
	public Collection<Revision> getRevisionHistory();
}
