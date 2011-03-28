package srl.visgo.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Workspace;
import srl.visgo.gui.zoom.PDocument;
import srl.visgo.gui.zoom.PDocumentGroup;

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

public class Visgo extends JFrame {
	public static void main(String[] args){
		//UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Visgo visgo = new Visgo();
				visgo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				visgo.setSize(1000,700);
				visgo.setVisible(true);
				visgo.load();
			}
		});
		NativeInterface.runEventPump();
	}

	DocsService client;
	public static PCanvas canvas;
	Visgo(){
		super("Visgo");
		canvas = new PCanvas();
		
		//an additional layer i am testing drag/drop with - Chris
//		PRoot root = canvas.getRoot();             
//		PCamera camera = canvas.getCamera();             
//		PLayer mainLayer =  canvas.getLayer();
//		PLayer pathLayer = new PLayer();             
//		root.addChild(pathLayer);             
//		camera.addLayer(0, pathLayer); 
//		
//		PSelectionEventHandler myselectionEventHandler = new PSelectionEventHandler(mainLayer, mainLayer);

		canvas.removeInputEventListener(canvas.getZoomEventHandler());
		canvas.removeInputEventListener(canvas.getPanEventHandler());
		
		VisgoMouseListener mouseListener = new VisgoMouseListener(canvas);
		canvas.addMouseWheelListener(mouseListener);
		//canvas.addMouseListener(mouseListener);
		//canvas.addMouseMotionListener(mouseListener);
		add(canvas);
	}

	private void load(){
		try{
			client = Login.getServiceLoggedIn();

			Workspace workspace = new Workspace(client);

			int i = 1;
			PDocumentGroup prevNode = null;
			ArrayList<PDocumentGroup> selectableParents = new ArrayList<PDocumentGroup>();
			for(DocumentGroup group : workspace.getDocumentGroups()){

				PDocumentGroup projectNode = new PDocumentGroup(group);
				projectNode.setColumnCount(3);
				projectNode.invalidate();
				if(prevNode!=null){
					PBounds bounds = prevNode.computeFullBounds(null);
					double yOffset =prevNode.getOffset().getY()+bounds.height+10;
					double xOffset = prevNode.getOffset().getX();
					projectNode.setOffset(xOffset, yOffset);
				}
				else{
					projectNode.setOffset(i*200, 100);
				}
				i++;
				canvas.getLayer().addChild(projectNode);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	PInputEventListener inputEventListener = new PInputEventListener(){

		@Override
		public void processEvent(PInputEvent arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	};
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
		// TODO Auto-generated method stub

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
