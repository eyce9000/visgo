package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.NSOption;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import edu.umd.cs.piccolo.PNode;

public class DocPanel extends JPanel {
	JWebBrowser browser;
	public static void main(String[] args){

		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Doc Panel Test");
				DocPanel panel = new DocPanel();
				frame.getContentPane().add(panel);
				frame.setBounds(100, 100, 600, 600);
				frame.setVisible(true);
			}
		});
		NativeInterface.runEventPump();
	}
	public DocPanel(){
		browser = new JWebBrowser(JWebBrowser.proxyComponentHierarchy());
		browser.setPreferredSize(new Dimension(600,600));
		this.add(browser,BorderLayout.CENTER);
		browser.navigate("http://docs.google.com");
	}
}
