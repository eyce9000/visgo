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
	
	public PingEvent(Object arg0, Collaborator collaborator) {
		super(arg0);
//		bounds = Visgo.workspace.getGlobalFullBounds();
		bounds = new PBounds(100, 100, 100, 100);
		creator = collaborator;
	}
	
	public PingEvent(Object arg0, Collaborator collaborator, PBounds callingBounds) {
		super(arg0);
		creator = collaborator;
		bounds = callingBounds;
	}
	
	public Collaborator getCreator(){
		return creator;
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
