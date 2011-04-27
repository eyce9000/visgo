package srl.visgo.util.chat.listeners;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
		String xmlString;
		try {
			xmlString = "<" + TAGNAME + " name = \""
				+ commandName 
				+ " \" >" 
				+ URLEncoder.encode(arguments,"UTF-8") 
				+ "</" +  TAGNAME  +">";

			return xmlString;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "";			
	}
	
	/**
	 * Function to parse the incoming xml into object
	 * @param xml
	 * @return
	 */
	public static CommandMessage parse(Message source, Element xml){
		
		//TODO : Finish implementing the parse method.
		try {
			String name = xml.getAttribute("name");
			String arguments;
			arguments = URLDecoder.decode(xml.getTextContent(),"UTF-8");
			CommandMessage result = new CommandMessage(source, name, arguments);
			return result;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
