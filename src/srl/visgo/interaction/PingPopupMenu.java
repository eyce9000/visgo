package srl.visgo.interaction;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import srl.visgo.data.listeners.PingEvent;
import srl.visgo.data.listeners.PingEventType;
import srl.visgo.gui.Visgo;

/**
 * A custom popup for sending Pings to other people in the workspace.
 *
 */
public class PingPopupMenu extends JPopupMenu{
	private static final long serialVersionUID = 1L;
	
	int x;
	int y;
	
	/**
	 * Create a new ping menu at the point of right click in canvas
	 */
	public PingPopupMenu(){
		super();
		JMenuItem pingAll = new JMenuItem("Send Ping");
	    pingAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Ping to all active users");
				System.out.println("at point: " + x + ", " + y);
				Visgo.workspace.sendPingEvent(new PingEvent(PingEventType.USER_PING, 
						Visgo.data.getCurrentCollaborator(), x, y));
				
			}
		});
	    
	    this.add(pingAll);
	    
	    //Location of right click on overall canvas gets popup menu
	    PBasicInputEventHandler popupListener = new PopupListener(this);
	    Visgo.canvas.addInputEventListener(popupListener);

	}
    
}


/**
 * Create a ping on right click.
 *
 */
class PopupListener extends PBasicInputEventHandler {
	PingPopupMenu popup;
	
	public PopupListener(PingPopupMenu menu){
		popup = menu;
	}
	
	@Override
	public void mousePressed(PInputEvent e) {
        maybeShowPopup(e);
    }

	@Override
    public void mouseReleased(PInputEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(PInputEvent e) {
        if (e.isPopupTrigger()) {
        	popup.x = (int) e.getPosition().getX();
        	popup.y = (int) e.getPosition().getY();
        	Point2D translate = Visgo.workspace.globalToLocal(new Point((int) e.getPosition().getX(), 
    						(int) e.getPosition().getY()));
            popup.show((Component) e.getComponent(), (int) translate.getX(), (int) translate.getY());
        }
    }
}

