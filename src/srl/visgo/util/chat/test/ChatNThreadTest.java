 package srl.visgo.util.chat.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import srl.visgo.util.chat.ChatManager;
import srl.visgo.util.chat.MessageCreator;
import srl.visgo.util.chat.MessageProcessor;
import srl.visgo.util.chat.listeners.CommandMessage;
import srl.visgo.util.chat.listeners.CommandMessageListener;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.GroupMessageListener;
import srl.visgo.util.chat.listeners.IndividualMessage;
import srl.visgo.util.chat.listeners.IndividualMessageListener;

public class ChatNThreadTest extends JFrame implements KeyListener,
CommandMessageListener, GroupMessageListener, 
IndividualMessageListener, WindowListener{

	private static boolean RECEIVECHAT = false;
	
	private String username = "hpi.test.1@gmail.com";
	private String password = "Visgo2011";
	
	private String targetID1 = "hpi.test.2@gmail.com";
	private String targetID2 = "dasarpjonam@gmail.com";
	private String targetID3 =  "eyce9000@gmail.com";
	private String targetID4 =  "heychrisaikens@gmail.com";
	
	private JLabel interpretedMessage = null;
	private JLabel currentMessage = null;
	
	private JTextField conversationTextEntry = null;
	
	private ChatManager conversationManager = null;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		ChatNThreadTest cntTest = new ChatNThreadTest();
		cntTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cntTest.setSize(1000,700);
		cntTest.setVisible(true);
			
	}
	
	public ChatNThreadTest(){		
		super("ChatNTestThread");
		
		conversationManager = new ChatManager(username, password);
		try
		{
			
			conversationManager.connect();		
		}
		catch (XMPPException xe){
			
			System.out.println("Cannot Connect to the gtalk server :");
		}
		catch (IllegalStateException ie){
			
			System.out.println("Cannot Connect to the gtalk server :");
		}
		
		MessageProcessor mi = conversationManager.getMessageInterpreter();
		mi.addCommandMessageListener(this);
		mi.addGroupMessageListener(this);
		mi.addIndividualMessageListener(this);
				
		conversationManager.createChat(targetID1, "1", null);	
		//conversationManager.createChat(targetID2, "2", null);	
		conversationManager.createChat(targetID3, "3", null);	
		conversationManager.createChat(targetID4, "4", null);	
		
		interpretedMessage = new JLabel();
		interpretedMessage.setBounds(20,20, 800, 250);
		interpretedMessage.setBackground(Color.darkGray);
		interpretedMessage.setForeground(Color.white);
		interpretedMessage.setText("interpreted Test");
		
		currentMessage = new JLabel();
		currentMessage.setBounds(20, 270, 800, 250);
		currentMessage.setBackground(Color.darkGray);
		currentMessage.setForeground(Color.white);
		currentMessage.setText(" current Test");
		
		conversationTextEntry = new JTextField();
		conversationTextEntry.setBounds(20, 550, 800, 200);
		conversationTextEntry.setBackground(Color.white);
		conversationTextEntry.setForeground(Color.darkGray);
		conversationTextEntry.addKeyListener(this);
		
		this.addWindowListener(this);
		getContentPane().add(interpretedMessage, BorderLayout.NORTH);
		getContentPane().add(currentMessage, BorderLayout.CENTER);
		getContentPane().add(conversationTextEntry, BorderLayout.SOUTH);
		this.getContentPane().setBackground(Color.DARK_GRAY);
		
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
			
			CommandMessage command = new CommandMessage(new Message(), "dummy" , conversationTextEntry.getText());
			MessageCreator mc = new MessageCreator(command);
			
			String body = mc.toXML();
			conversationManager.sendGroupMessage(body);
		}
	}
	

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CommandReceived(Chat conversation, CommandMessage notification) {
		// TODO Auto-generated method stub
		
		currentMessage.setText(((Message)notification.getSource()).getBody());
		interpretedMessage.setText(notification.getCommandName() + " ::: " 
				+ notification.getArguments());
		
	}

	@Override
	public void IncomingGroupMessage(Chat from, GroupMessage currentGMessage) {
		// TODO Auto-generated method stub
		
		currentMessage.setText(((Message)currentGMessage.getSource()).getBody());
		interpretedMessage.setText(currentGMessage.getMessage());
	}

	@Override
	public void IncomingIndividualMessage(Chat from,
			IndividualMessage currentIMessage) {
		// TODO Auto-generated method stub
		
		currentMessage.setText(((Message)currentIMessage.getSource()).getBody());
		interpretedMessage.setText(currentIMessage.getMessage());
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		this.conversationManager.disconnect();
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

	

}
