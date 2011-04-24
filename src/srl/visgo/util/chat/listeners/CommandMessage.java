package srl.visgo.util.chat.listeners;

import java.util.EventObject;

import org.xml.sax.*;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.*;
import javax.xml.parsers.*;	

/***
 * The Command Message class 
 * - name of the command
 * - arguments of the message in string format.
 * The arguments should be re-interpreted to work with VISGO
 * 
 * the  xml schema is 
 * <command name = "name of the command" > arguments space separated </command>
 * 
 * Any modification to the schema will affect the following methods.
 * toXML ()
 * parse ()
 * @author manoj
 *
 */
public class CommandMessage extends EventObject{
	
	public static final String TAGNAME = "command";

	private String commandName = null; // name of the command
	 
	private String arguments = null; // the arguments in a string format.

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	
	public CommandMessage(Message source,String commandName, String arguments){
		super(source);
		this.commandName = commandName;
		this.arguments = arguments;
	}
	
	public CommandMessage(String commandName, String arguments){
		super(new Message());
		this.commandName = commandName;
		this.arguments = arguments;
	}
	
	/**
	 * Use this method to get the xml representation of the object
	 * @return
	 */
	public String toXML(){
		String xmlString = "<" + TAGNAME + " name = \""
			+ commandName 
			+ " \" > " 
			+ arguments 
			+ "</" +  TAGNAME  +">"; 
		
		return xmlString;
					
	}
	
	/**
	 * Function to parse the incoming xml into object
	 * @param xml
	 * @return
	 */
	public static CommandMessage parse(Message source, Element xml){
		
		//TODO : Finish implementing the parse method.
		String name = xml.getAttribute("name");
		String arguments = xml.getTextContent();
		
		CommandMessage result = new CommandMessage(source, name, arguments);
		return result;
		
	}
}
