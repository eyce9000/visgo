package srl.visgo.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DocPanel extends JPanel {
	/*
	public static void main(String[] args){

		JFrame frame = new JFrame("Doc Panel Test");
		DocPanel panel = new DocPanel();
		frame.getContentPane().add(panel);
		frame.setBounds(100, 100, 600, 600);
		frame.setVisible(true);
	}
	public DocPanel(){
		Display display = new Display();
		final Shell shell = new Shell(display);
		final Browser browser;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: " + e.getMessage());
			return;
		}

		shell.open();
		browser.setUrl("http://eclipse.org");
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	*/
}
