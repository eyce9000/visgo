package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.google.gdata.util.InvalidEntryException;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.gui.DocPanel;
import srl.visgo.gui.Resources;
import srl.visgo.gui.Visgo;
import srl.visgo.gui.interaction.VisgoDragEventHandler;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;
import gDocsFileSystem.GFileSystem;

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
		backgroundNode.addInputEventListener(new PDragEventHandler());

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
        checkLocation(aNode);
		
	}
	@Override
	public void mouseDragged(PInputEvent event){
		
	}
	
	@Override
	public void mousePressed(PInputEvent event){
		//Is doc in a group?
		if(mDocument.getParent().getParent().getClass().equals(srl.visgo.gui.zoom.PDocumentGroup.class))
		{
			//Remove from group
			PLayer layer = Visgo.canvas.getLayer();
			PDocumentGrid oldGroup = (PDocumentGrid) mDocument.getParent();
			for(int i = 0; i < oldGroup.getChildrenCount(); i++)
			{
				System.out.println(oldGroup.getChild(i).toString());
			}
			
			/**
			 * TODO: Need the actual location of the document so that it can be placed at that point
			 * outside of its old group once added to the main workspace layer
			 */
			final Point2D spot = mDocument.getFullBounds().getCenter2D();
			mDocument.getParent().removeChild(mDocument);
			layer.addChild(mDocument);
			mDocument.setX(spot.getX());
			mDocument.setY(spot.getY());
		}
	}
	
	/**
	 * check if the new location of the node is within a group 
	 * @param aNode
	 * @throws Exception 
	 */
	public void checkLocation(PNode aNode){
		PLayer layer = Visgo.canvas.getLayer();
		PDocumentGroup group;
		PDocument doc;
		DocumentGroup oldGroup = mDocument.getDocument().getParent();
		//PDocumentGroup oldPGroup = mDocument.getDocument().getParent().getPDocGroup();
		PDocumentGroup oldPGroup = null;
		
		for(int i = 0; i < layer.getChildrenCount(); i++)
		{
			if(layer.getChild(i).getClass().equals(srl.visgo.gui.zoom.PDocumentGroup.class))
			{
				//The node is a group of documents
				group = (PDocumentGroup) layer.getChild(i);
				System.out.println(group.getDocumentGroup().getName());

				/**********************
				 * TODO: Need a better way to check if dropped into a group! This bounds check is invalid
				 **********************/
				if(group.computeFullBounds(null).contains(aNode.getBounds().getCenter2D()))
				{
					//Dropped into same group
					if(group.getDocumentGroup().getDocuments().contains(mDocument.getDocument()))
					{
							System.out.println(">--< Back into same group");
					}
					else 	//Dropped into a new group
					{
						//Remove from old drag group
						System.out.println("<-- " + mDocument.getDocument().getName() + " removed from group: " + oldGroup.getName());
						oldGroup.removeDocument(mDocument.getDocument());
						oldPGroup.invalidate();

						//Add to new drop group
						System.out.println("--> " + mDocument.getDocument().getName() + " added to group: " + group.getDocumentGroup().getName());
						group.getDocumentGroup().addDocument(mDocument.getDocument());
						//Visgo.systemTest.insertEntry(mDocument.getDocument(), group.getDocumentGroup(), true);	//TODO: Move to adding files from local to workspace
						//Visgo.systemTest.setParent(mDocument.getDocument(), group.getDocumentGroup(), true);
						
						group.invalidate();
					}
				}
			}
			else if(layer.getChild(i).getClass().equals(srl.visgo.gui.zoom.PDocument.class))
			{
				//The node is a free-floating document
				doc = (PDocument) layer.getChild(i);
				if(doc.getDocument().getName().equals(mDocument.getDocument().getName()))
				{
					System.out.println(doc.getDocument().getName());
				}
			}
		}

		System.out.println();
	}
	
}
