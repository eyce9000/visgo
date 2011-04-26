package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import srl.visgo.data.Document;
import srl.visgo.gui.listeners.CloseDocumentEvent;
import srl.visgo.gui.listeners.CloseDocumentListener;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class DocumentEditPanel extends JPanel implements ActionListener {
	JWebBrowser browser;
	JButton closeButton;
	Document currentDoc;
	
	Queue<CloseDocumentListener> listeners = new LinkedList<CloseDocumentListener>();
	public static void main(String[] args){
		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Doc Panel Test");
				DocumentEditPanel panel = new DocumentEditPanel();
				frame.getContentPane().add(panel);
				frame.setBounds(100, 100, 600, 600);
				frame.setVisible(true);
			}
		});
		NativeInterface.runEventPump();
	}
	
	
	public DocumentEditPanel(){
		super();
		this.setLayout(new BorderLayout());
		browser = new JWebBrowser(JWebBrowser.proxyComponentHierarchy());
		browser.setPreferredSize(new Dimension(1000,1000));
		
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		this.setPreferredSize(new Dimension(1000,1000));
		
		this.add(browser,BorderLayout.CENTER);
		this.add(closeButton,BorderLayout.NORTH);
		browser.navigate("http://docs.google.com");
	}
	public void setDocument(Document doc){
		currentDoc = doc;
		browser.navigate(doc.getHref());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==closeButton){
			onClose();
		}
	}
	
	public void addCloseDocumentListener(CloseDocumentListener listener){
		listeners.add(listener);
	}
	
	private void onClose(){
		CloseDocumentEvent event = new CloseDocumentEvent(currentDoc);
		for(CloseDocumentListener listener:listeners){
			listener.onCloseDocument(event);
		}
	}
}
