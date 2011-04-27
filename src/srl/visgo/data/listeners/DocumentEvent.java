package srl.visgo.data.listeners;

import java.util.EventObject;

import srl.visgo.data.Document;

public class DocumentEvent extends EventObject {
	public enum Type{
		Modified,
		Created,
		Moved
	}
	
	private Type mType;
	
	public DocumentEvent(Document arg0, Type type) {
		super(arg0);
		mType = type;
	}
	
	public Type getType(){
		return mType;
	}
	public Document getDocument(){
		return (Document)this.getSource();
	}

}
