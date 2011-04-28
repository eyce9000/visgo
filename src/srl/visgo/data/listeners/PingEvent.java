package srl.visgo.data.listeners;

import java.awt.Color;
import java.util.EventObject;

import srl.visgo.data.Collaborator;
import srl.visgo.gui.Visgo;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Create a ping for other people
 */
public class PingEvent extends EventObject{
	private static final long serialVersionUID = 1L;

	Collaborator creator;
	PingEventType type;
	int originX;
	int originY;
	
	public PingEvent(PingEventType type, Collaborator collaborator, int x, int y) {
		super(type);
		this.type = type;
		creator = collaborator;
		originX = x;
		originY = y;
	}
	
	public Collaborator getCreator(){
		return creator;
	}
	
	public PingEventType getType(){
		return type;
	}
	
	public int getX(){
		return originX;
	}
	
	public int getY(){
		return originY;
	}
	
	/**
	 * Animate the view to that of the creator's ping origin
	 */
	public void moveToBounds(){
//		PPath test = PPath.createEllipse(100, 100, 100, 50);
//		test.setPaint(Color.green);
//		test.setVisible(true);
//		Visgo.canvas.getLayer().addChild(test);
//		Visgo.canvas.invalidate();
	}

}
