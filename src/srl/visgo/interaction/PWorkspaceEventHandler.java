package srl.visgo.interaction;

import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
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
	ObjectMapper mapper = new ObjectMapper();
	
	
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
        	PingEvent newPing = new PingEvent(PingEventType.USER_PING, 
					Visgo.data.getCurrentCollaborator(), x, y);

			try {
				String serialized = mapper.writeValueAsString(PingEvent.serialize(newPing));
				Visgo.data.setCommandMessage("ping", serialized);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	Visgo.data.getCurrentCollaborator().setPing(newPing);
			Visgo.workspace.sendPingEvent(newPing);
//			PingPopupMenu pop = new PingPopupMenu();
			
		}
		else if(event.getClickCount() == 2){			
//		PBounds test = Visgo.workspace.getGlobalFullBounds();
//		Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 1000);
		}
	}
}