package srl.visgo.gui.zoom;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import srl.visgo.data.Document;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class PDocumentEditor extends PNode{

	public PDocumentEditor(Document doc){
		super();
		mDocument = doc;
		panel = new EditorPanel(doc);
		this.addChild(new PSwing(panel));
	}
	Document mDocument;
	EditorPanel panel;
}
class EditorPanel extends JPanel {
	JWebBrowser browser;
	public static void main(String[] args){

		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		/*SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Doc Panel Test");
				EditorPanel panel = new EditorPanel();
				frame.getContentPane().add(panel);
				frame.setBounds(100, 100, 600, 600);
				frame.setVisible(true);
			}
		});
		*/
		NativeInterface.runEventPump();
	}
	public EditorPanel(Document doc){
		browser = new JWebBrowser(JWebBrowser.proxyComponentHierarchy());
		browser.setPreferredSize(new Dimension(600,600));
		this.add(browser,BorderLayout.CENTER);
		browser.navigate(doc.getHref());
	}
}

