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

/***
 * Example to illustrate how to send messages
 * 
 * - use the ChatManager to login . use connect() to login and use disconnect() to logout
 * - USe the createChat() method to add members to the group
 * - Use the MessageInterpreter and listeners to receive message/ commands
 * - USe the sendGroup/Individual Message/Command () methods to send messages
 * @author manoj
 *
 */
public class ChatNThreadTest extends JFrame implements KeyListener,
CommandMessageListener, GroupMessageListener, 
IndividualMessageListener, WindowListener{

	private static boolean RECEIVECHAT = false;
	
	private String username = "hpi.test.2@gmail.com";
	private String password = "Visgo2011";
	
	private String targetID1 = "hpi.test.1@gmail.com";
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
		
		
		//Use the CHat Manager to login to the gtalk
		conversationManager = new ChatManager(username, password);
		try
		{
			//connecting to gtalk 
			conversationManager.connect();		
		}
		catch (XMPPException xe){
			
			System.out.println("Cannot Connect to the gtalk server :");
		}
		catch (IllegalStateException ie){
			
			System.out.println("Cannot Connect to the gtalk server :");
		}
		
		//Use the message interpreter -
		MessageProcessor mi = conversationManager.getMessageInterpreter();
		// add your self to the listeners to listen to command, message and group message separately
		mi.addCommandMessageListener(this);
		mi.addGroupMessageListener(this);
		mi.addIndividualMessageListener(this);
				
		//Use the Create Chat to add members to the group
		// The members should be part of your IM list. External members cannot receiver chat messages
		conversationManager.createChat(targetID1, "1", null);	
		//conversationManager.createChat(targetID2, "2", null);	
		conversationManager.createChat(targetID3, "3", null);	
		//conversationManager.createChat(targetID4, "4", null);	
		
		interpretedMessage = new JLabel();
		interpretedMessage.setBounds(20,20, 800, 200);
		interpretedMessage.setBackground(Color.darkGray);
		interpretedMessage.setForeground(Color.white);
		interpretedMessage.setText("interpreted Test");
		
		currentMessage = new JLabel();
		currentMessage.setBounds(20, 270, 800, 200);
		currentMessage.setBackground(Color.darkGray);
		currentMessage.setForeground(Color.white);
		currentMessage.setText(" current Test");
		
		conversationTextEntry = new JTextField();
		conversationTextEntry.setBounds(20, 500, 800, 200);
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
		
		
		////Example for creating each type of message packet and sending
		// it to individual or group in a VISGO
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
			
			// Example to create a command 
			CommandMessage command = new CommandMessage(new Message(), "dummy" , conversationTextEntry.getText());
			
			//Example to create a message to individual
			IndividualMessage iMessage = new IndividualMessage(new Message(), conversationTextEntry.getText());
			
			//Example to create a message for the group
			GroupMessage gMessage = new GroupMessage(new Message(), conversationTextEntry.getText());
			
			//Sending the command notification to individual - choose the target person
			conversationManager.sendIndividualCommand(targetID3, command);
			
			//Sending the command to the group
			conversationManager.sendGroupCommand(command);
			
			//Sending messages to individual - choose the target person
			conversationManager.sendIndividualMessage(targetID3, iMessage);
			
			//Sending message to the group
			conversationManager.sendGroupMessage(gMessage);
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
	public void CommandReceived(CommandMessage notification) {
		// TODO Auto-generated method stub
		
		currentMessage.setText(((Message)notification.getSource()).getBody());
		interpretedMessage.setText(notification.getCommandName() + " ::: " 
				+ notification.getArguments());
		
	}

	@Override
	public void IncomingGroupMessage(GroupMessage currentGMessage) {
		// TODO Auto-generated method stub
		
		currentMessage.setText(((Message)currentGMessage.getSource()).getBody());
		interpretedMessage.setText(currentGMessage.getMessage());
	}

	@Override
	public void IncomingIndividualMessage(IndividualMessage currentIMessage) {
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
