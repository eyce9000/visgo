package srl.visgo.util.chat.test;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class ReceiveChatTest extends JFrame implements WindowListener, MessageListener, PacketListener{
	
	XMPPConnection serverConnection = null;
	
	private String username = "dasarpjonam@gmail.com";
	private String password = "ganw1301";
	
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
		Chat newConversation = null;
		
		try {
			
			serverConnection.connect();
		
			serverConnection.login(this.username, this.password);
									
			newConversation = serverConnection.getChatManager().createChat(targetID1, "200", this);
			
			PacketTypeFilter filter = new PacketTypeFilter(Message.class);
			serverConnection.addPacketListener(this, filter);
			
			newConversation.sendMessage("hi");
		
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.addWindowListener(this);
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		serverConnection.disconnect();
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

	@Override
	public void processPacket(Packet arg0) {
		// TODO Auto-generated method stub
		Message msg = (Message)arg0;
		
		System.out.println("Message ::" + msg.getBody());
	}

}
