package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
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
	JWebBrowser currentBrowser;
	JButton closeButton;
	Document currentDoc;

	
	HashMap<String,JWebBrowser> openBrowsers = new HashMap<String,JWebBrowser>();
	
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
		currentBrowser = new JWebBrowser(JWebBrowser.proxyComponentHierarchy());
		currentBrowser.setBarsVisible(false);
		currentBrowser.setButtonBarVisible(false);
		currentBrowser.setPreferredSize(new Dimension(1000,1000));
		this.add(currentBrowser,BorderLayout.CENTER);
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		this.add(closeButton,BorderLayout.NORTH);
		
		this.setPreferredSize(new Dimension(1000,1000));
	}
	public void setDocument(Document doc){
		currentDoc = doc;
		
		currentBrowser.navigate(doc.getHref());
		
		/*
		if(currentBrowser!=null){
			currentBrowser.setVisible(false);
		}
		currentBrowser = getBrowser(doc);
		currentBrowser.setVisible(true);
		*/
		this.revalidate();
	}
	
	public JWebBrowser getBrowser(Document doc){
		if(openBrowsers.containsKey(doc.getHref())){
			return openBrowsers.get(doc.getHref());
		}
		else{
			JWebBrowser browser = new JWebBrowser(JWebBrowser.proxyComponentHierarchy());
			browser.setBarsVisible(false);
			browser.setButtonBarVisible(false);
			browser.setPreferredSize(new Dimension(1000,1000));
			browser.navigate(doc.getHref());
			openBrowsers.put(doc.getHref(), browser);
			this.add(browser,BorderLayout.CENTER);
			return browser;
		}
		
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
