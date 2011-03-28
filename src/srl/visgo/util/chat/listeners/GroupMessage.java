package srl.visgo.util.chat.listeners;

import java.util.EventObject;

import org.xml.sax.*;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.*;
import javax.xml.parsers.*;	

/**
 * Class represents the message sent to the whole group by the VISGO.
 * xml schema used
 * 
 * <GMessage> message text </GMessage>
 * Any modification to the schema will affect the following methods.
 * toXML ()
 * parse ()
 * @author manoj
 *
 */

public class GroupMessage extends EventObject {
	
	public static final String TAGNAME = "gmessage"; // the tagname associated with the message
	
	private String message = null; // the text message sent to the group

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public GroupMessage(Message source, String message){
		super(source);
		this.message = message;
	}
	
	public GroupMessage(String message){
		super(null);
		this.message = message;
	}
	
	/**
	 * Use the to xml method to get the xml representation of the message
	 * @return
	 */
	public String toXML(){
		
		String xmlString = "<" + TAGNAME + ">" 
		+ message
		+ "</" + TAGNAME + ">";
		
		return xmlString;
	}
	
	
	public static GroupMessage parse(Message source, Element xml){
		//TODO: write the parsing logic
		
		GroupMessage result = new GroupMessage(source, xml.getTextContent());
		
		return null;
	}

}
