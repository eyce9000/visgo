package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.geom.Point2D;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.gui.Resources;
import srl.visgo.gui.Visgo;
import srl.visgo.gui.interaction.VisgoDragEventHandler;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class PDocument extends PNode {
	static Color BACK_COLOR = Color.GRAY;
	static Color SELECT_COLOR = Color.orange;
	
	Document mDocument;
	PImage imageNode = new PImage();
	PText textNode;
	PPath backgroundNode;
	PDocumentEventHandler eventHandler;
	VisgoDragEventHandler dragger;
	
	
	//Have title and image be grouped together as a single, movable node
	public PDocument(Document document){
		super();
		mDocument = document;

		backgroundNode = PPath.createRoundRectangle(0f, 0f, 50, 50, 5f, 5f);
		backgroundNode.setPaint(BACK_COLOR);
		
		this.addChild(backgroundNode);	
		backgroundNode.setVisible(true);
		

		String shortTitle = document.getName();
		if(shortTitle.length()>10){
			shortTitle = shortTitle.substring(0, 10)+"...";
		}
		
		textNode = new PText(shortTitle);
		imageNode.setImage(Resources.getImage("doc.png"));
		
		backgroundNode.addChild(imageNode);
		backgroundNode.addChild(textNode);
//		backgroundNode.addInputEventListener(new PDragEventHandler());

		//prevents dragging off names/images from the overall node
		for(int i = 0; i < backgroundNode.getChildrenCount(); i++)
			backgroundNode.getChild(i).setPickable(false);
		
		double h = imageNode.getHeight();
		double w = imageNode.getWidth();
		double tw = textNode.getWidth();
		double th = textNode.getHeight();
		textNode.setOffset(new Point2D.Double(0,h));
		imageNode.setOffset(new Point2D.Double(w/2,0));
		backgroundNode.setOffset(0,0);
		
		//makes background as big as text + image area
		backgroundNode.setWidth(this.getWidth() + ((shortTitle.length() > 10) ? tw : tw + w/2));
		backgroundNode.setHeight(this.getHeight() + h + th);
		this.setX(tw/2);
		
		eventHandler = new PDocumentEventHandler(this);
		this.addInputEventListener(eventHandler);
	}
	
	//get the document behind this PDoc
	public Document getDocument(){
		return mDocument;
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
		aNode.setPaint(Color.GREEN);
        checkLocation(mDocument);
		
	}
	@Override
	public void mouseDragged(PInputEvent event){
		if(event.getClickCount() > 1)
			return;
		//Is doc in a group or free?
		if(mDocument.getParent().equals(Visgo.canvas.getLayer()))
		{
			
		}
		else if(mDocument.getParent().getParent().getParent().getClass().equals(srl.visgo.gui.zoom.PDocumentGroup.class))
		{
			//Remove from group
			PLayer layer = Visgo.canvas.getLayer();
			PDocumentGroup oldGroup = (PDocumentGroup) mDocument.getParent().getParent().getParent();
			
			final Point2D spot = mDocument.getGlobalFullBounds().getCenter2D();
			oldGroup.removeDocument(mDocument);
			layer.addChild(mDocument);
			mDocument.setOffset(spot);
			mDocument.backgroundNode.addInputEventListener(new PDragEventHandler());
		}
	}
	
	@Override
	public void mousePressed(PInputEvent event){

	}
	
	/**
	 * Handles the dropping of a PDocument. PDocs can be dropped onto the main canvas, into
	 * existing groups, or onto another canvas PDoc for form a new group.
	 * @param aNode
	 * @throws Exception 
	 */
	public void checkLocation(PDocument aNode){
		PLayer layer = Visgo.canvas.getLayer();
		
		for(int i = 0; i < layer.getChildrenCount(); i++)
		{
			if(layer.getChild(i).getClass().equals(srl.visgo.gui.zoom.PDocumentGroup.class))
			{
				PDocumentGroup test = (PDocumentGroup) layer.getChild(i);
				final Point2D spot = aNode.getGlobalFullBounds().getCenter2D();
				//Was the doc dropped into a group?
				if(test.getGlobalFullBounds().contains(spot))
				{
					test.addDocument(aNode);
					layer.removeChild(mDocument);
					break;
				}
			}
			else if(layer.getChild(i).getClass().equals(srl.visgo.gui.zoom.PDocument.class))
			{
				PDocument test = (PDocument) layer.getChild(i);
				if(test.equals(mDocument)) continue;
				final Point2D spot = aNode.getGlobalFullBounds().getCenter2D();
				//Was the doc dropped onto another doc?
				if(test.getGlobalFullBounds().contains(spot))
				{
					//TODO: Add prompt for new group name
					PDocumentGroup newGroup = new PDocumentGroup(new DocumentGroup("New group!"));
					newGroup.addDocument(test);
					newGroup.addDocument(mDocument);
					layer.removeChild(i);
					layer.removeChild(mDocument);
					layer.addChild(newGroup);
					newGroup.setOffset(spot);
					break;
				}
			}
		}
	}
	
}
