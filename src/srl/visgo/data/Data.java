package srl.visgo.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.util.ServiceException;

import gDocsFileSystem.GDatabase;

public class Data {
	private DocumentRoot documentRoot;
	public GDatabase database;
	private HashMap<String,Collaborator> collaborators;
	private DocsService docsService;

	private LinkedList<DataListener> listeners;

	public Data(){
		listeners = new LinkedList<DataListener>();
		collaborators = new HashMap<String,Collaborator>();
		docsService = new DocsService("VISGO-V1");
	}
	public void reloadAll(){
		reloadDocumentRoot();
	}
	public void reloadDocumentRoot(){
		try {
			if(documentRoot == null){
				documentRoot = new DocumentRoot(docsService);
			}
			else{
				documentRoot.reload(docsService);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DocumentRoot getDocumentRoot(){
		return documentRoot;
	}
	
	public Collaborator getCollaborator(String username){
		return collaborators.get(username);
	}
	public Collection<Collaborator> getAllCollaborators(){
		return collaborators.values();
	}
	public synchronized void fireDataChange(DataEventType type){
		for(DataListener listener:listeners){
			listener.onDataUpdate(type);
		}
	}
	public void addDataListener(DataListener listener){
		listeners.add(listener);
	}
}
