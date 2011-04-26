package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;

import srl.visgo.data.Data;
import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Workspace;
import srl.visgo.data.listeners.PingEvent;
import srl.visgo.data.listeners.PingListener;
import srl.visgo.gui.chat.ChatPanel;
import srl.visgo.gui.zoom.PDocument;
import srl.visgo.gui.zoom.PDocumentGroup;
import srl.visgo.gui.zoom.PWorkspace;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import gDocsFileSystem.GDatabase;
import gDocsFileSystem.GFileSystem;

public class Visgo extends JFrame implements PingListener{
	public static void main(String[] args){



		//UIUtils.setPreferredLookAndFeel();
		//NativeInterface.open();
		//SwingUtilities.invokeLater(new Runnable() {
			//public void run() {
				//Login.getCredentials();
				Login.username = "hpi.test.2@gmail.com";
				Login.password = "Visgo2011";
				Visgo.data = new Data();

				Visgo visgo = new Visgo();
				visgo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				visgo.setSize(1000,700);
				visgo.setVisible(true);
			//}
		//});
		//NativeInterface.runEventPump();

	}

	DocsService docsService;
	public static PCanvas canvas;
	public static Data data;
	ChatPanel chatPanel;
	public static PWorkspace workspace;

	Visgo(){
		super("Visgo - "+Login.username);

		//Visgo.data = new Data();
		Container contentPane = this.getContentPane();

		chatPanel = new ChatPanel();
		CreateDocsPanel createDocsPanel = new CreateDocsPanel(data.workspace);

		canvas = new PCanvas();
		//		try {
		//			systemTest = new GFileSystem("visgo.workspace");
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		canvas.removeInputEventListener(canvas.getZoomEventHandler());
		canvas.removeInputEventListener(canvas.getPanEventHandler());

		VisgoMouseListener mouseListener = new VisgoMouseListener(canvas);
		canvas.addMouseWheelListener(mouseListener);
		//canvas.addMouseListener(mouseListener);
		//canvas.addMouseMotionListener(mouseListener);
		contentPane.add(canvas,BorderLayout.CENTER);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(createDocsPanel);
		leftPanel.add(chatPanel);

		contentPane.add(leftPanel, BorderLayout.WEST);
		load();
        PBounds test = workspace.getGlobalFullBounds();
		Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 100);
	}

	private void load(){
		workspace = new PWorkspace();
		canvas.getLayer().addChild(workspace);
		workspace.addPingListener(this);
	}
	


	PInputEventListener inputEventListener = new PInputEventListener(){

		@Override
		public void processEvent(PInputEvent arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void onPing(PingEvent e) {
		e.moveToBounds();
	}
}

class VisgoMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener{
	PCanvas mCanvas;
	VisgoMouseListener(PCanvas canvas){
		mCanvas = canvas;
	}

	float MIN_SCALE = 1;
	float MAX_SCALE = 5;
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		PCamera camera = mCanvas.getCamera();
		double currentScale = camera.getViewScale();
		double scaleDelta = (1.0f + (-0.01f * arg0.getWheelRotation()));
		double newScale = currentScale * scaleDelta;

		if (newScale < MIN_SCALE)
		{
			newScale = MIN_SCALE;
			scaleDelta = 1;
		}
		if ((MAX_SCALE > 0) && (newScale > MAX_SCALE))
		{
			newScale = MAX_SCALE;
			scaleDelta = 1;
		}
		//System.out.println(newScale);
		//camera.setViewScale(newScale);

		Point2D mousePoint = new Point2D.Float(arg0.getX(),arg0.getY());
		Point2D localPoint = camera.globalToLocal(mousePoint);
		Point2D viewPoint = camera.localToView(localPoint);
		camera.scaleViewAboutPoint(scaleDelta, viewPoint.getX(), viewPoint.getY());
		//canvas.getCamera().scaleViewAboutPoint();
		//ea.Handled = true;

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		PCamera camera = mCanvas.getCamera();
		double cameraX = camera.getX();
		double cameraY = camera.getY();
		System.out.println(cameraX+", "+cameraY);
		cameraX+=10;
		cameraY+=10;
		camera.setX(cameraX);
		camera.setY(cameraY);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
		{
			Rectangle bounds = mCanvas.getBounds();
			mCanvas.getCamera().setViewBounds(bounds);
		}
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
