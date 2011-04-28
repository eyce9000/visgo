package srl.visgo.gui.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import srl.visgo.data.Collaborator;
import srl.visgo.data.listeners.PingEventType;
import srl.visgo.gui.Login;
import srl.visgo.gui.Visgo;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.GroupMessageListener;
import srl.visgo.util.chat.listeners.StatusChangeListener;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel implements GroupMessageListener,ActionListener{

	JScrollPane mScroll;
	JPanel mMessagesPanel;
	TextInputPanel mTextInputPanel;
	CollaboratorListPanel mCollaboratorListPanel;
	public ChatPanel(){
		super(new BorderLayout());
		mMessagesPanel = new JPanel();
		mMessagesPanel.setLayout(new BoxLayout(mMessagesPanel,BoxLayout.Y_AXIS));
		mMessagesPanel.setBackground(Color.WHITE);

		mScroll = new JScrollPane(mMessagesPanel);
		//mMessagesPanel.setPreferredSize(new Dimension(200,500));
		mTextInputPanel = new TextInputPanel();
		mTextInputPanel.mSendButton.addActionListener(this);

		mCollaboratorListPanel = new CollaboratorListPanel();
		
		this.add(mScroll,BorderLayout.CENTER);
		this.add(mTextInputPanel,BorderLayout.SOUTH);
		this.add(mCollaboratorListPanel, BorderLayout.NORTH);
		Visgo.data.addGroupMessageListener(this);
		mScroll.setAutoscrolls(true);
	}

	@Override
	public void IncomingGroupMessage(GroupMessage currentMessage) {
		final GroupMessage message = currentMessage;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

//				System.out.println(message.getMessage());
				String from = ((Message)message.getSource()).getFrom().split("@gmail\\.com")[0]+"@gmail.com";
				addMessage(message.getMessage(),from);
			}
		});

	}
	
	public void addMessage(String body, String from){
		/*JTextArea message = new JTextArea(from+": "+body);
		message.setEditable(false);
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setPreferredSize(message.getPreferredSize());
		mMessagesPanel.add(message);
		*/
		mMessagesPanel.add(new MessagePanel(body,from));
		mMessagesPanel.revalidate();
		mScroll.revalidate();
	}
	
	/**
	 * Signal that the given collaborator created a ping.
	 * @param creator
	 * @param type 
	 */
	public void addPing(Collaborator creator, PingEventType type){
		mCollaboratorListPanel.addPing(creator, type);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getSource() == mTextInputPanel.mSendButton){
			GroupMessage message = Visgo.data.sendGroupMessage(mTextInputPanel.mMessageField.getText());
			addMessage(message.getMessage(),Login.username);
			mTextInputPanel.mMessageField.setText("");
			mTextInputPanel.mMessageField.setCaretPosition(0);
			mTextInputPanel.mMessageField.setRows(1);
		}
	}

}

@SuppressWarnings("serial")
class CollaboratorListPanel extends JPanel implements StatusChangeListener{
	ArrayList<CollaboratorPanel> panels;
	
	CollaboratorListPanel(){
		super();
		panels = new ArrayList<CollaboratorPanel>();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setPreferredSize(new Dimension(200,200));
		Visgo.data.addStatusChangeListener(this);
		this.resetList();
	}
	
	private void resetList(){
		this.removeAll();
		panels.clear();
		Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
		for(Collaborator collaborator: collaborators){
			panels.add(new CollaboratorPanel(collaborator));
		}
		for(CollaboratorPanel p : panels){
			this.add(p);
		}
		this.revalidate();
	}
	
	public void addPing(Collaborator creator, PingEventType type){
		for(CollaboratorPanel p : panels){
			if(p.mCollaborator.equals(creator)){
				p.addPing(type);
				this.revalidate();
				return;
			}
		}
	}
	
	@Override
	public void StatusChanged(String userID, Type status) {
		System.out.println("Status changed:"+userID+" "+status);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				resetList();
			}
		});
	}
}
@SuppressWarnings("serial")
class CollaboratorPanel extends JPanel{
	Collaborator mCollaborator;
	JLabel name;
	CollaboratorPanelListener listener;
	Timer timer;
	
	CollaboratorPanel(Collaborator collaborator){
		super(new BorderLayout());
		mCollaborator = collaborator;
		listener = new CollaboratorPanelListener(this);
		this.addMouseListener(listener);
		JPanel colorSwatch = new JPanel(){
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(20,20);
			}
		};
		colorSwatch.setBackground(collaborator.getColor());
		this.add(colorSwatch,BorderLayout.WEST);
		name = new JLabel(collaborator.getName());

		if(collaborator.getStatus() != Presence.Type.available){
			name.setForeground(Color.GRAY);
//			this.setVisible(false);
		}
		this.add(name,BorderLayout.CENTER);
	}
	
	/**
	 * Indicate that the panel's collaborator has generated a ping.
	 * @param type 
	 */
	public void addPing(PingEventType type){
		if(type == PingEventType.USER_PING) {
		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent actionEvent) {
			        if(name.getForeground().equals(Color.black)){
				  		setBackground(Color.orange);
						setOpaque(true);
						name.setForeground(Color.white);
			        }
			        else{
			        	setOpaque(false);
			    		name.setForeground(Color.black);
			        }
		        }
		      };
		      if(timer == null)
		    	  timer = new Timer(1000, actionListener);
		      if(!timer.isRunning())
		    	  timer.start();
		}
		
		else if(type == PingEventType.DOCUMENT_ADDED){
			//TODO: Change to only have it flash their name once and then go away!
		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent actionEvent) {
			        if(name.getForeground().equals(Color.black)){
				  		setBackground(Color.green);
						setOpaque(true);
						name.setForeground(Color.white);
			        }
			        else{
			        	setOpaque(false);
			    		name.setForeground(Color.black);
			        }
		        }
		      };
		      if(timer == null)
		    	  timer = new Timer(3000, actionListener);
		      if(!timer.isRunning()){
		    	  timer.setInitialDelay(0);
		    	  timer.setRepeats(false);
		    	  timer.start();
		      }
		}
	}
}

/**
 * Custom class for handling clicks on collaborators above the chat box.  
 *
 */
class CollaboratorPanelListener implements MouseListener {
	CollaboratorPanel mPanel;
	
	public CollaboratorPanelListener(CollaboratorPanel panel){
		mPanel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(mPanel.timer != null && mPanel.timer.isRunning()){
			mPanel.timer.stop();
			mPanel.setOpaque(false);
			mPanel.name.setForeground(Color.black);
			mPanel.revalidate();
			mPanel.getParent().invalidate();
			mPanel.getParent().repaint();
			Visgo.chatPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			//TODO: On click, move to the spot
			Visgo.workspace.goToPing(mPanel.mCollaborator);
			
		}	
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(mPanel.timer != null && mPanel.timer.isRunning()){
			Visgo.chatPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(mPanel.timer != null && mPanel.timer.isRunning()){
			Visgo.chatPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
}

@SuppressWarnings("serial")
class MessagePanel extends JPanel{
	JTextArea mMessageArea;
	MessagePanel(String body, String from){
		super(new BorderLayout());
		mMessageArea = new JTextArea();
		mMessageArea.setText(body);
		mMessageArea.setEditable(false);
		mMessageArea.setLineWrap(true);
		mMessageArea.setWrapStyleWord(true);
		mMessageArea.setOpaque(false);
		mMessageArea.setMargin(new Insets(0,5,0,0));
		Collaborator collaborator = Visgo.data.getCollaborator(from);
		JPanel colorSwatch = new JPanel(){
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(20,20);
			}
			@Override
			public Dimension getMaximumSize(){
				return new Dimension(20,Integer.MAX_VALUE);
			}
		};
		this.setBackground(Color.WHITE);
		colorSwatch.setBackground(collaborator.getColor().brighter());
		this.add(colorSwatch,BorderLayout.WEST);
		this.add(mMessageArea,BorderLayout.CENTER);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return mMessageArea.getSize();
	}
	@Override
	public Dimension getMinimumSize(){
		return mMessageArea.getSize();
	}
	@Override
	public Dimension getMaximumSize(){
		Dimension preferred = mMessageArea.getPreferredSize();
		preferred.width = 300;
		return preferred;
	}
}

@SuppressWarnings("serial")
class TextInputPanel extends JPanel{
	JButton mSendButton;
	JTextArea mMessageField;

	TextInputPanel(){
		super();
		mSendButton = new JButton("Send");
		mMessageField = new JTextArea();
		mMessageField.setColumns(15);
		mMessageField.setRows(1);
		mMessageField.setLineWrap(true);
		mMessageField.setWrapStyleWord(true);
		
		mMessageField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Enter pressed");
				mSendButton.doClick();
			}
		});
		
		this.add(mMessageField,BorderLayout.CENTER);
		this.add(mSendButton, BorderLayout.EAST);
	}
}