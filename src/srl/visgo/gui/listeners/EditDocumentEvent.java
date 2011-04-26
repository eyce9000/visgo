package srl.visgo.gui.listeners;

import java.util.EventObject;

import srl.visgo.data.Document;
import srl.visgo.gui.zoom.PDocument;

public class EditDocumentEvent extends EventObject {

	public EditDocumentEvent(Document doc) {
		super(doc);
	}

}
