package srl.visgo.util.chat;

import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.IndividualMessage;

public class MessageCreator {

	public static final String TAGNAME = "visgo";
	
	private CommandMessage command = null;
	
	private GroupMessage gMessage = null;
	
	private IndividualMessage iMessage = null;

	public CommandMessage getCommand() {
		return command;
	}

	public void setCommand(CommandMessage command) {
		this.command = command;
	}

	public GroupMessage getGMessage() {
		return gMessage;
	}

	public void setGMessage(GroupMessage message) {
		gMessage = message;
	}

	public IndividualMessage getIMessage() {
		return iMessage;
	}

	public void setIMessage(IndividualMessage message) {
		iMessage = message;
	}
	
	public MessageCreator(CommandMessage command){
		
		this.command = command;
	}
	
	public MessageCreator(GroupMessage gMessage){
		
		this.gMessage = gMessage;
	}

	public MessageCreator(IndividualMessage iMessage){
		
		this.iMessage = iMessage;
	}
	
	public String toXML(){
		
		String xmlString = "<" + TAGNAME + ">";
		
		if(command != null)
			xmlString += command.toXML();
		
		if(gMessage != null)
			xmlString += gMessage.toXML();
		
		if(iMessage != null)
			xmlString += iMessage.toXML();
		
		xmlString += "</" + TAGNAME + ">";
		
		return xmlString;
	}
}
