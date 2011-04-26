package srl.visgo.gui.listeners;

import java.util.EventObject;

import srl.visgo.data.Document;

public class CloseDocumentEvent extends EventObject {

	public CloseDocumentEvent(Document doc) {
		super(doc);
	}

}
