package srl.visgo.data.listeners;


public interface DocumentListener {
	public void onDocumentModified(DocumentEvent event);
	public void onDocumentCreated(DocumentEvent event);
	public void onDocumentMoved(DocumentEvent event);
}
