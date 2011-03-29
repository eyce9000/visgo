package srl.visgo.util.chat.listeners;

import java.util.EventObject;

import org.xml.sax.*;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.*;
import javax.xml.parsers.*;	

/**
 * Class represents the message sent to one person by the VISGO.
 * xml schema used
 * 
 * <imessage> message text </imessage>
 * 
 * THINGS BELOW ARE NOT IMPLEMENTED YET
 * - add the location if needed as attributes.
 * EX: <imessage xloc = ## yloc = ##> message text </imessage>
 * 
 * Any modification to the schema will affect the following methods.
 * toXML ()
 * parse ()
 * 
 * @author manoj
 *
 */

public class IndividualMessage extends EventObject {

public static final String TAGNAME = "imessage"; // the tagname associated with the message
	
	private String message = null; // the text message sent to the group

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public IndividualMessage(Message source, String message){
		super(source);
		this.message = message;
	}
	
	public IndividualMessage(String message){
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
	
	public static IndividualMessage parse(Message source, Element xml){
		
		IndividualMessage result = new IndividualMessage(source, xml.getTextContent());
		return result;
	}

}
