package srl.visgo.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;

import srl.visgo.data.Collaborator;
import srl.visgo.data.Data;
import srl.visgo.data.Document;
import srl.visgo.data.listeners.PingEvent;
import srl.visgo.data.listeners.PingListener;
import srl.visgo.gui.chat.ChatPanel;
import srl.visgo.gui.listeners.CloseDocumentEvent;
import srl.visgo.gui.listeners.CloseDocumentListener;
import srl.visgo.gui.listeners.EditDocumentEvent;
import srl.visgo.gui.listeners.EditDocumentListener;
import srl.visgo.gui.zoom.PWorkspace;
import srl.visgo.interaction.PWorkspaceEventHandler;
import srl.visgo.interaction.PingPopupMenu;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;

public class Visgo extends JFrame implements PingListener,EditDocumentListener,CloseDocumentListener{
	public static void main(String[] args){
		
		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Login.getCredentials();
				//
				Login.username = "hpi.test.2@gmail.com";
				Login.password = "Visgo2011";
				Visgo.data = new Data();

				Visgo.instance = new Visgo();
				Visgo.instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Visgo.instance.setSize(1000,700);
				Visgo.instance.setVisible(true);
			}
		});
		NativeInterface.runEventPump();
	}

	DocsService docsService;
	public static PCanvas canvas;
	public static Data data;
	public static Visgo instance;
	ChatPanel chatPanel;
	public static PWorkspace workspace;
	DocumentEditPanel editPanel;

	private Visgo(){
		super("Visgo - "+Login.username);
		//browser.setPreferredSize(new Dimension(600,600));
		editPanel = new DocumentEditPanel();
		editPanel.setVisible(false);
		editPanel.setOpaque(false);

		Container contentPane = this.getContentPane();

		chatPanel = new ChatPanel();
		CreateDocsPanel createDocsPanel = new CreateDocsPanel(data.workspace);

		canvas = new PCanvas();
		//canvas.setPreferredSize(new Dimension(1000,1000));

		canvas.removeInputEventListener(canvas.getZoomEventHandler());
		canvas.removeInputEventListener(canvas.getPanEventHandler());

		//VisgoMouseListener mouseListener = new VisgoMouseListener(canvas);
//		canvas.addMouseWheelListener(mouseListener);
		
		JPanel centerPanel = new JPanel();
		OverlayLayout layout = new OverlayLayout(centerPanel);
		centerPanel.setLayout(layout);
		centerPanel.add(canvas);
		centerPanel.add(editPanel);
		
		contentPane.add(centerPanel,BorderLayout.CENTER);
		
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
		workspace.addEditDocumentListener(this);
		canvas.addInputEventListener(new PWorkspaceEventHandler(workspace));
		editPanel.addCloseDocumentListener(this);
		canvas.getLayer().addChild(workspace);
		data.workspace.startBackgroudThreads();
		workspace.addPingListener(this);
	}
	
	public void loadEditDocument(Document doc){
		editPanel.setDocument(doc);
	}

	@Override
	public void onEditDocument(EditDocumentEvent event) {
		canvas.setVisible(false);
		editPanel.setVisible(true);
		editPanel.revalidate();
	}

	@Override
	public void onCloseDocument(CloseDocumentEvent event) {
		editPanel.setVisible(false);
		editPanel.revalidate();
		canvas.setVisible(true);
		canvas.revalidate();
        PBounds test = workspace.getGlobalFullBounds();
		Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 500);
	}

	PInputEventListener inputEventListener = new PInputEventListener(){

		@Override
		public void processEvent(PInputEvent arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void onPing(PingEvent e) {
		//TODO: Indicate a ping has been received
		Collection<Collaborator> collaborators = Visgo.data.getAllCollaborators();
		Collaborator self = Visgo.data.getCurrentCollaborator();
		
		//Ignore self created pings
		if(self == e.getCreator()){
			System.out.println("I Created the PING");
			chatPanel.addPing(e.getCreator());

			return;
		}
		
		//Show ping's origin in Collaborator menu
		chatPanel.addPing(e.getCreator());
		
		//TODO: Click visual indicator causes something... 
//		e.moveToBounds();
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
