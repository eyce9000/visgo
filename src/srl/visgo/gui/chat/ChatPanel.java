package srl.visgo.gui.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import srl.visgo.data.Collaborator;
import srl.visgo.data.Data;
import srl.visgo.data.DataEventType;
import srl.visgo.data.DataListener;
import srl.visgo.gui.Login;
import srl.visgo.gui.Visgo;
import srl.visgo.util.chat.listeners.GroupMessage;
import srl.visgo.util.chat.listeners.GroupMessageListener;

public class ChatPanel extends JPanel implements GroupMessageListener,ActionListener{

	JScrollPane mScroll;
	JPanel mMessagesPanel;
	TextInputPanel mTextInputPanel;
	public ChatPanel(){
		super(new BorderLayout());
		mMessagesPanel = new JPanel();
		mMessagesPanel.setLayout(new BoxLayout(mMessagesPanel,BoxLayout.Y_AXIS));
		mMessagesPanel.setBackground(Color.WHITE);

		mScroll = new JScrollPane(mMessagesPanel);
		//mMessagesPanel.setPreferredSize(new Dimension(200,500));
		mTextInputPanel = new TextInputPanel();
		mTextInputPanel.mSendButton.addActionListener(this);

		this.add(mScroll,BorderLayout.CENTER);
		this.add(mTextInputPanel,BorderLayout.SOUTH);
		this.add(new CollaboratorListPanel(), BorderLayout.NORTH);
		Visgo.data.addGroupMessageListener(this);
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

class CollaboratorListPanel extends JPanel implements DataListener{
	CollaboratorListPanel(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setPreferredSize(new Dimension(200,200));
		onDataUpdate(DataEventType.COLLABORATOR_ADDED);
	}
	@Override
	public void onDataUpdate(DataEventType type) {
		if(type == DataEventType.COLLABORATOR_ADDED){
			Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
			for(Collaborator collaborator: collaborators){
				this.add(new CollaboratorPanel(collaborator));
			}
		}
	}
}
class CollaboratorPanel extends JPanel{
	Collaborator mCollaborator;
	CollaboratorPanel(Collaborator collaborator){
		super(new BorderLayout());
		mCollaborator = collaborator;
		JPanel colorSwatch = new JPanel(){
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(20,20);
			}
		};
		colorSwatch.setBackground(collaborator.getColor());
		this.add(colorSwatch,BorderLayout.WEST);
		this.add(new JLabel(collaborator.getName()),BorderLayout.CENTER);
	}
}

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