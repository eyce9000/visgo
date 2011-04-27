package srl.visgo.gui.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import srl.visgo.data.Collaborator;
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
				// TODO Auto-generated method stub
				System.out.println(message.getMessage());
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
	
	public void addPing(Collaborator creator){
		mCollaboratorListPanel.pingInList(creator);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
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
	CollaboratorListPanel(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setPreferredSize(new Dimension(200,200));
		Visgo.data.addStatusChangeListener(this);
		this.resetList();
	}
	
	private void resetList(){
		this.removeAll();
		Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
		for(Collaborator collaborator: collaborators){
			this.add(new CollaboratorPanel(collaborator, false));
		}
		this.revalidate();
	}
	
	//TODO: Make it so more than one ping can be up at a time, aka, just change name and not recreate list...
	public void pingInList(Collaborator creator){
		this.removeAll();
		Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
		for(Collaborator collaborator: collaborators){
			if(collaborator == creator)
				this.add(new CollaboratorPanel(collaborator, true));
			else
				this.add(new CollaboratorPanel(collaborator, false));
		}
		this.revalidate();
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
	CollaboratorPanel(Collaborator collaborator, boolean pingSource){
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
		if(pingSource){
			name.setForeground(Color.ORANGE);
			
		}
		if(collaborator.getStatus() != Presence.Type.available){
			name.setForeground(Color.GRAY);
			this.setVisible(false);
		}
		this.add(name,BorderLayout.CENTER);
	}
}

class CollaboratorPanelListener implements MouseListener {
	CollaboratorPanel mPanel;
	
	public CollaboratorPanelListener(CollaboratorPanel panel){
		mPanel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Remove ping marker!");
		mPanel.name.setBackground(Color.black);
		mPanel.revalidate();
		mPanel.getParent().invalidate();
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
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