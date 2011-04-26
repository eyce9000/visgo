package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import srl.visgo.data.Document;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class DocumentEditPanel extends JPanel {
	JWebBrowser browser;
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
		this.add(browser,BorderLayout.CENTER);
		browser.navigate("http://docs.google.com");
	}
	public void setDocument(Document doc){
		browser.navigate(doc.getHref());
	}
}
