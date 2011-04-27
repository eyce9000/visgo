package srl.visgo.interaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import srl.visgo.data.listeners.PingEvent;
import srl.visgo.gui.Visgo;

/**
 * A custom popup for sending Pings to other people in the workspace.
 *
 */
public class PingPopupMenu extends JPopupMenu{
	private static final long serialVersionUID = 1L;
	
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
				
				Visgo.workspace.sendPingEvent(new PingEvent(Visgo.workspace, Visgo.data.getCurrentCollaborator()));
				
			}
		});
	    
	    this.add(pingAll);
	    
	    //Location of right click on overall canvas gets popup menu
	    MouseListener popupListener = new PopupListener(this);
	    Visgo.canvas.addMouseListener(popupListener);

	}




    
}

class PopupListener extends MouseAdapter {
	PingPopupMenu popup;
	
	public PopupListener(PingPopupMenu menu){
		popup = menu;
	}
	
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }
}

