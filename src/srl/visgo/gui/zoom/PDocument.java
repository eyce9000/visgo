package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TrayIcon.MessageType;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.google.gdata.model.gd.CreateId;

import chrriis.dj.nativeswing.swtimpl.Message;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Revision;
import srl.visgo.gui.Resources;
import srl.visgo.gui.Visgo;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PHtmlView;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PDocument extends PNode {
	static Color BACK_COLOR = Color.getHSBColor(200, 200, 200);
	static Color SELECT_COLOR = Color.orange;

	Document mDocument;
	PImage imageNode = new PImage();
	PHtmlView textNode;
	PPath backgroundNode;
	PDocumentEventHandler eventHandler;
	PDragEventHandler dragHandler;
	PDocumentGroup currentGroup;
	PRevisionActivity activityBar;

	private boolean invalid = false;


	//Have title and image be grouped together as a single, movable node
	public PDocument(Document document){
		super();
		mDocument = document;
		rebuild();
	}

	private void rebuild(){
		this.removeAllChildren();

		backgroundNode = PPath.createRoundRectangle(0f, 0f, 50, 50, 5f, 5f);
		backgroundNode.setPaint(BACK_COLOR);

		this.addChild(backgroundNode);	
		backgroundNode.setVisible(true);

		String title = mDocument.getName();

		textNode = new PHtmlView("<p align=\"center\">"+title+"</p>");
		textNode.setBounds(0, 0, 80, 20);

		String type = mDocument.getListEntry().getType();
		if(type.compareTo("document") == 0)
		{
			imageNode.setImage(Resources.getImage("document.png"));
		}
		else if(type.compareTo("drawing") == 0)
		{
			imageNode.setImage(Resources.getImage("drawing.png"));
		}
		else if(type.compareTo("pdf") == 0)
		{
			imageNode.setImage(Resources.getImage("pdf.png"));
		}
		else if(type.compareTo("presentation") == 0)
		{
			imageNode.setImage(Resources.getImage("presentation.png"));
		}
		else if(type.compareTo("spreadsheet") == 0)
		{
			imageNode.setImage(Resources.getImage("spreadsheet.png"));
		}
		else
		{
			imageNode.setImage(Resources.getImage("file.png"));
		}

		backgroundNode.addChild(imageNode);
		backgroundNode.addChild(textNode);
		dragHandler = new PDragEventHandler();
		backgroundNode.addInputEventListener(dragHandler);

		//prevents dragging off names/images from the overall node

		double h = imageNode.getHeight();
		double w = imageNode.getWidth();
		double tw = textNode.getWidth();
		double th = textNode.getHeight();
		textNode.setOffset(new Point2D.Double(0,h));
		imageNode.setOffset(new Point2D.Double((tw/2)-(w/2),0));
		backgroundNode.setOffset(0,0);

		Collection<Revision> revisions = mDocument.getRevisionHistory();
		/*List<Revision> revisions = Arrays.asList(new Revision[]{
				new Revision(Visgo.data.getCollaborator("hpi.test.2@gmail.com"),System.currentTimeMillis()-150000),
				new Revision(Visgo.data.getCollaborator("heychrisaikens@gmail.com"),System.currentTimeMillis()-250000),
				new Revision(Visgo.data.getCollaborator("eyce9000@gmail.com"),System.currentTimeMillis()-10000),
		});*/
		activityBar = new PRevisionActivity(revisions,PRevisionActivity.Orientation.Vertical);
		activityBar.setOffset(tw,10);
		activityBar.setPickable(false);
		backgroundNode.addChild(activityBar);
		//makes background as big as text + image area
		backgroundNode.setWidth(tw);
		backgroundNode.setHeight(h + th);
		this.setX(tw/2);

		eventHandler = new PDocumentEventHandler(this);
		this.addInputEventListener(eventHandler);
		this.setOffset(mDocument.getOffsetX(), mDocument.getOffsetY());

		for(int i = 0; i < backgroundNode.getChildrenCount(); i++)
			backgroundNode.getChild(i).setPickable(false);

		invalid = false;
	}

	public void invalidate(){
		invalid = true;
		rebuild();
		if(this.getParent()!=null)
			this.getParent().repaint();
	}

	//get the document behind this PDoc
	public Document getDocument(){
		return mDocument;
	}

	public void removeDragHandler(){
		backgroundNode.removeInputEventListener(eventHandler);
	}
}

class PDocumentEventHandler extends PBasicInputEventHandler{
	PDocument mDocument;

	PDocumentEventHandler(PDocument document){
		mDocument = document;
	}

	@Override
	public void mouseExited(PInputEvent event){
		mDocument.backgroundNode.setPaint(PDocument.BACK_COLOR);
	}

	@Override
	public void mouseEntered(PInputEvent event){
		mDocument.backgroundNode.setPaint(PDocument.SELECT_COLOR);
	}

	@Override
	public void mouseReleased(PInputEvent event){
		PNode aNode = event.getPickedNode();
		//TODO: Time off for this paint
		aNode.setPaint(Color.GREEN);
		checkLocation(mDocument);

	}
	@Override
	public void mouseClicked(PInputEvent event){
		if(event.getClickCount() == 2){
			event.setHandled(true);
			PBounds test = mDocument.getGlobalFullBounds();
			PTransformActivity animation = Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 700);

			final Document doc = this.mDocument.mDocument;
			Visgo.instance.loadEditDocument(doc);
			PActivity.PActivityDelegate delegate = new PActivity.PActivityDelegate() {
				@Override
				public void activityStepped(PActivity arg0) {}
				@Override
				public void activityStarted(PActivity arg0) {}

				@Override
				public void activityFinished(PActivity arg0) {
					Visgo.workspace.onEditDocument(doc);

				}
			};
			animation.setDelegate(delegate);
		}
	}

	@Override
	public void mouseDragged(PInputEvent event){
		event.setHandled(true);
		//Is doc in a group or free?
		if(mDocument.getParent().equals(Visgo.workspace))
		{

		}
		else if(mDocument.getParent().getParent().getParent() instanceof srl.visgo.gui.zoom.PDocumentGroup)
		{
			//Remove from group
			PNode layer = Visgo.workspace;
			PDocumentGroup oldGroup = (PDocumentGroup) mDocument.getParent().getParent().getParent();

			final Point2D spot = mDocument.getGlobalFullBounds().getCenter2D();
			oldGroup.greyDocument(mDocument);
			mDocument.currentGroup = oldGroup;
			layer.addChild(mDocument);
			mDocument.setOffset(spot);
			//			mDocument.backgroundNode.addInputEventListener(new PDragEventHandler());
		}
	}

	@Override
	public void mousePressed(PInputEvent event){

	}

	/**
	 * Handles the dropping of a PDocument. PDocs can be dropped onto the main canvas, into
	 * existing groups, or onto another canvas PDoc for form a new group. 
	 * 
	 * Prompts for new group name if a doc is dropped on another. Canceling the resulting 
	 * dialog prevents new group's formation.
	 * @param aNode
	 * @throws Exception 
	 */
	public void checkLocation(PDocument aNode){
		PNode layer = Visgo.workspace;
		boolean onWorkspace = true;

		for(int i = 0; i < layer.getChildrenCount(); i++)
		{
			//Checks for groups and documents
			if(layer.getChild(i) instanceof srl.visgo.gui.zoom.PDocumentGroup)
			{
				PDocumentGroup test = (PDocumentGroup) layer.getChild(i);
				final Point2D spot = aNode.getGlobalFullBounds().getCenter2D();
				//Was the doc dropped into a group?
				if(test.getGlobalFullBounds().contains(spot))
				{
					test.addDocument(aNode);
					layer.removeChild(mDocument);
					mDocument.removeDragHandler();
					//Remove from old group if it's not the same drop location
					if(mDocument.currentGroup != null && !mDocument.currentGroup.equals(test))
						mDocument.currentGroup.removeDocument(mDocument);
					mDocument.currentGroup = test;

					mDocument.currentGroup.mDocumentGroup.addDocument(mDocument.mDocument);
					mDocument.mDocument.setOffsetX(0);
					mDocument.mDocument.setOffsetY(0);
					System.out.println("Move Document:"+mDocument.mDocument.getOffsetX()+","+
							mDocument.mDocument.getOffsetY());
					mDocument.mDocument.save();
					onWorkspace = false;
					break;
				}
			}
			else if(layer.getChild(i) instanceof srl.visgo.gui.zoom.PDocument)
			{
				PDocument test = (PDocument) layer.getChild(i);
				if(test.equals(mDocument)) continue;
				final Point2D spot = aNode.getGlobalFullBounds().getCenter2D();
				//Was the doc dropped onto another doc?
				if(test.getGlobalFullBounds().contains(spot))
				{
					//Prompt for new group name
					String name = null;
					ImageIcon icon = new ImageIcon("image/newgroup2.png");
					name = (String) JOptionPane.showInputDialog(
							null, "Enter group name:",
							"Create a new group", JOptionPane.PLAIN_MESSAGE, 
							icon, null, 
					"new group");
					if(name != null){
						//New group desired
						PDocumentGroup newGroup = new PDocumentGroup(DocumentGroup.createGroup(name));
						newGroup.addDocument(test);
						newGroup.addDocument(mDocument);
						layer.removeChild(i);
						layer.removeChild(mDocument);
						layer.addChild(newGroup);
						newGroup.setOffset(spot);
						mDocument.currentGroup.removeDocument(mDocument);
						mDocument.currentGroup = newGroup;
						onWorkspace = false;
						mDocument.mDocument.setOffsetX(0);
						mDocument.mDocument.setOffsetY(0);
						System.out.println("Move Document:"+mDocument.mDocument.getOffsetX()+","+
								mDocument.mDocument.getOffsetY());
						mDocument.mDocument.save();
					}
					break;
				}
			}
			else
			{
				if(mDocument.currentGroup!=null){
					mDocument.currentGroup.removeDocument(mDocument);
				}


				mDocument.mDocument.setOffsetX(mDocument.getFullBounds().x);
				mDocument.mDocument.setOffsetY(mDocument.getFullBounds().y);

				System.out.println("Move Document:"+mDocument.mDocument.getOffsetX()+","+
						mDocument.mDocument.getOffsetY());
				mDocument.mDocument.save();
				mDocument.setGlobalScale(.75);
				break;

			}
		}
		if(onWorkspace)
		{
			if(mDocument.currentGroup!=null){
				mDocument.currentGroup.removeDocument(mDocument);
			}

			mDocument.mDocument.setOffsetX(mDocument.getFullBounds().x);
			mDocument.mDocument.setOffsetY(mDocument.getFullBounds().y);

			System.out.println("Move Document:"+mDocument.mDocument.getOffsetX()+","+
					mDocument.mDocument.getOffsetY());
			mDocument.mDocument.save();
			mDocument.setGlobalScale(.75);

		}
	}

}
