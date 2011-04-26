package srl.visgo.data.listeners;

import java.util.EventObject;

import srl.visgo.gui.Visgo;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Create a ping for other people
 *	TODO: Be able to target certain people
 */
public class PingEvent extends EventObject{
	private static final long serialVersionUID = 1L;

	final PBounds bounds;
	PBounds returnBounds;
	
	public PingEvent(Object arg0) {
		super(arg0);
//		bounds = Visgo.workspace.getGlobalFullBounds();
		bounds = new PBounds(100, 100, 100, 100);
	}
	
	public PingEvent(Object arg0, PBounds callingBounds) {
		super(arg0);
		bounds = callingBounds;
	}
	
	/**
	 * Animate the view to that of the creator's ping origin
	 */
	public void moveToBounds(){
		returnBounds = Visgo.workspace.getGlobalFullBounds();
		Visgo.canvas.getCamera().animateViewToCenterBounds(bounds.getBounds2D(), true, 1000);
	}

}
