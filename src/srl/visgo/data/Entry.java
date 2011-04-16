package srl.visgo.data;

public interface Entry {
	public String getName();
	public void setParent(DocumentGroup parent);
	public DocumentGroup getParent();
	public boolean hasParent();
	public String getId();
	public double getOffsetX();
	public double getOffsetY();
}
