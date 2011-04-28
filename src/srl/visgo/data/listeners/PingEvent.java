package srl.visgo.data.listeners;

import java.awt.Color;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

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
	int originX;
	int originY;
	
	public PingEvent(PingEventType type, Collaborator collaborator, int x, int y) {
		super(type);
		this.type = type;
//		bounds = Visgo.workspace.getGlobalFullBounds();
		bounds = new PBounds(100, 100, 100, 100);
		creator = collaborator;
		originX = x;
		originY = y;
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
		returnBounds = Visgo.workspace.getGlobalFullBounds();
//		PPath test = PPath.createEllipse(100, 100, 100, 50);
//		test.setPaint(Color.green);
//		test.setVisible(true);
//		Visgo.canvas.getLayer().addChild(test);
//		Visgo.canvas.invalidate();
		Visgo.canvas.getCamera().animateViewToCenterBounds(bounds.getBounds2D(), true, 1000);
	}
	
	public static Map serialize(PingEvent event){
		Map m = new HashMap();
		m.put("creator", event.getCreator().getUsername());
		m.put("x", event.getX()+"");
		m.put("y",event.getY()+"");
		m.put("type",event.getType().toString());
		return m;
	}
	public static PingEvent deserialize(Map m){
		PingEventType type = PingEventType.valueOf(m.get("type").toString());
		Collaborator collab = Visgo.data.getCollaborator(m.get("creator").toString());
		int x = Integer.parseInt(m.get("x").toString());
		int y = Integer.parseInt(m.get("y").toString());
		return new PingEvent(type,collab,x,y);
	}
}
