package srl.visgo.data.listeners;

import java.awt.Color;
import java.util.EventObject;

import srl.visgo.data.Collaborator;
import srl.visgo.gui.Visgo;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Create a ping for other people
 *	TODO: Be able to target certain people, return to old bounds
 *	TODO: Add ping indicator to screen. On click, moveToBounds()
 */
public class PingEvent extends EventObject{
	private static final long serialVersionUID = 1L;

	PBounds bounds;
	PBounds returnBounds;
	Collaborator creator;
	PingEventType type;
	
	public PingEvent(PingEventType type, Collaborator collaborator) {
		super(type);
		this.type = type;
//		bounds = Visgo.workspace.getGlobalFullBounds();
		bounds = new PBounds(100, 100, 100, 100);
		creator = collaborator;
	}
	
	public PingEvent(PingEventType type, Collaborator collaborator, PBounds callingBounds) {
		super(type);
		this.type = type;
		creator = collaborator;
		bounds = callingBounds;
	}
	
	public Collaborator getCreator(){
		return creator;
	}
	
	public PingEventType getType(){
		return type;
	}
	
	/**
	 * Animate the view to that of the creator's ping origin
	 */
	public void moveToBounds(){
		returnBounds = Visgo.workspace.getGlobalFullBounds();
//		PPath test = PPath.createEllipse(100, 100, 100, 50);
//		test.setPaint(Color.green);
//		test.setVisible(true);
//		Visgo.canvas.getLayer().addChild(test);
//		Visgo.canvas.invalidate();
		Visgo.canvas.getCamera().animateViewToCenterBounds(bounds.getBounds2D(), true, 1000);
	}

}
