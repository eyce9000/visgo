package srl.visgo.gui.interaction;

import srl.visgo.gui.Visgo;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This class is designed to handle creating and handling visual pings to other users.
 * A PNotification is a visual cue that someone wants your attention.
 *
 */
public class PNotification extends PNode{
	private static final long serialVersionUID = 1L;
	
	
	final PBounds bounds;
	PBounds returnBounds;
	
	
	/**
	 * Create a new notification and have the current bounds become the ping bounds
	 */
	public PNotification(){
		super();
		bounds = Visgo.workspace.getGlobalFullBounds();
	}
	
	/**
	 * Create a new ping and store the creator's current bounds. This allows the pinged
	 * person to quickly move to the creator's view of the workspace.
	 * @param callingBounds
	 */
	public PNotification(PBounds callingBounds){
		super();
		bounds = callingBounds;
		//TODO: Give a size/location/make visible
		
	}
	

	
	/**
	 * Get the bounds of the creator when they sent the ping.
	 * @return
	 */
	public PBounds getCallingBounds(){
		return bounds;
	}
	
	/**
	 * Animate the view to that of the creator's ping origin
	 */
	public void moveToBounds(){
		returnBounds = Visgo.workspace.getGlobalFullBounds();
		Visgo.canvas.getCamera().animateViewToCenterBounds(bounds.getBounds2D(), true, 100);
	}
}
