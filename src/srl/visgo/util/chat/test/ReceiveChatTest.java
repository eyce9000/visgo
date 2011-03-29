package srl.visgo.util.chat.test;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class ReceiveChatTest extends JFrame implements WindowListener, MessageListener{
	
	XMPPConnection serverConnection = null;
	
	private String username = "hpi.test.2@gmail.com";
	private String password = "Visgo2011";
	
	private String targetID1 = "hpi.test.1@gmail.com";
	private String targetID2 = "dasarpjonam@gmail.com";
	private String targetID3 =  "eyce9000@gmail.com";
	private String targetID4 =  "heychrisaikens@gmail.com";
	

	public static void main(String[] args) {
		
		ReceiveChatTest rct = new ReceiveChatTest();
		rct.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rct.setSize(1000,700);
		rct.setVisible(true);
				
	}
	
	public ReceiveChatTest(){
		
		ConnectionConfiguration cc = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		
		serverConnection = new XMPPConnection(cc);
		
		try {
			
			serverConnection.connect();
		
				
			serverConnection.login(this.username, this.password, "VISGO2");
			
			Chat newConversation = serverConnection.getChatManager().createChat(targetID1, "1", this);
			
			newConversation.sendMessage("hi");
		
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		serverConnection.disconnect();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processMessage(Chat arg0, Message arg1) {
		// TODO Auto-generated method stub
		System.out.println(" Message ::" + arg1.getBody());
	}

}
