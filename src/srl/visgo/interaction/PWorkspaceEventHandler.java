package srl.visgo.interaction;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import srl.visgo.data.listeners.PingEvent;
import srl.visgo.data.listeners.PingEventType;
import srl.visgo.gui.Visgo;
import srl.visgo.gui.zoom.PWorkspace;

/**
 * Custom event handler designed to detect mouse events within the workspace.
 *
 */
public class PWorkspaceEventHandler extends PBasicInputEventHandler{
	PWorkspace workspace;
	
	
	public PWorkspaceEventHandler(PWorkspace space){
		workspace = space;
	}
	
	@Override
	public void mouseClicked(PInputEvent event){

		//Look for and consume right clicks as Pings
		if (event.getButton() == MouseEvent.BUTTON3) { 
			event.setHandled(true);
			int x = (int) event.getPosition().getX();
        	int y = (int) event.getPosition().getY();
			Visgo.workspace.sendPingEvent(new PingEvent(PingEventType.USER_PING, 
					Visgo.data.getCurrentCollaborator(), x, y));
//			PingPopupMenu pop = new PingPopupMenu();
			
		}
		else if(event.getClickCount() == 2){
			//workspace.sendPingEvent(new PingEvent(this));
			
//			PBounds test = Visgo.workspace.getGlobalFullBounds();
//			Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 1000);
		}
	}
}