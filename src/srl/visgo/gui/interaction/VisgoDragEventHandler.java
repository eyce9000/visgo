package srl.visgo.gui.interaction;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/***
 * Custom class to handle dragging and dropping Documents around the workspace
 * @author Chris
 *
 */
public class VisgoDragEventHandler extends PDragEventHandler{
	Document mDocument;
	DocumentGroup mGroup;
	
	/**
	 * Constructor for dragging documents
	 * @param doc
	 */
	public VisgoDragEventHandler(Document doc){
		super();
		mDocument = doc;
	}
	
	/**
	 * Constructor for dragging a group
	 * @param group
	 */
	public VisgoDragEventHandler(DocumentGroup group){
		super();
		mGroup = group;
		
	}
	
}
