package srl.visgo.gui.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import srl.visgo.data.Collaborator;
import srl.visgo.data.Data;
import srl.visgo.data.DataEventType;
import srl.visgo.data.DataListener;
import srl.visgo.gui.Visgo;

public class ChatPanel extends JPanel implements MessageListener{

	JScrollPane mScroll;
	JPanel mMessagesPanel;
	TextInputPanel mTextInputPanel;
	public ChatPanel(){
		super();
		mMessagesPanel = new JPanel();
		mScroll = new JScrollPane(mMessagesPanel);
		mTextInputPanel = new TextInputPanel();

		this.add(mScroll,BorderLayout.CENTER);
		this.add(mTextInputPanel,BorderLayout.SOUTH);
	}

	public void addMessage(Message message){
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		chat.getParticipant();

		MessagePanel messagePanel = new MessagePanel(message);
		mMessagesPanel.add(messagePanel);
	}

}

class CollaboratorListPanel extends JPanel implements DataListener{
	CollaboratorListPanel(){
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
		super();
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
	MessagePanel(Message message){
		super();
		String body = message.getBody();
		String from = message.getFrom();

		Collaborator collaborator = Visgo.data.getCollaborator(from);

		JLabel bodyLabel = new JLabel(body);
		bodyLabel.setOpaque(false);

		this.add(bodyLabel);
		this.setBackground(collaborator.getColor().brighter());
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

		this.add(mMessageField,BorderLayout.CENTER);
		this.add(mSendButton, BorderLayout.EAST);
	}
}